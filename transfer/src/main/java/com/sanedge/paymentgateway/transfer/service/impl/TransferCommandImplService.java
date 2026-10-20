package com.sanedge.paymentgateway.transfer.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.adapter.card.CardPort;
import com.sanedge.paymentgateway.common.adapter.model.Saldo;
import com.sanedge.paymentgateway.common.adapter.saldo.SaldoPort;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.enums.Status;
import com.sanedge.paymentgateway.common.exception.ResourceNotFoundException;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.common.outbox.Outbox;
import com.sanedge.paymentgateway.common.outbox.OutboxEventFactory;
import com.sanedge.paymentgateway.common.outbox.OutboxRepository;
import com.sanedge.paymentgateway.transfer.domain.requests.CreateTransferRequest;
import com.sanedge.paymentgateway.transfer.domain.requests.UpdateTransferRequest;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponse;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseDeleteAt;
import com.sanedge.paymentgateway.transfer.models.Transfer;
import com.sanedge.paymentgateway.transfer.repository.TransferCommandRepository;
import com.sanedge.paymentgateway.transfer.repository.TransferQueryRepository;
import com.sanedge.paymentgateway.transfer.service.TransferCommandService;

import io.grpc.StatusRuntimeException;
import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferCommandImplService implements TransferCommandService {

    private static final String TOPIC_TRANSFER_CREATE = "email-service-topic-transfer-create";

    private final TransferCommandRepository transferCommandRepository;
    private final TransferQueryRepository transferQueryRepository;
    private final CardPort cardPort;
    private final SaldoPort saldoPort;
    private final TracingMetrics tracingMetrics;
    private final OutboxRepository outboxRepository;

    @Value("${notification.transfer.email:admin@example.com}")
    private String notificationEmail;

    @Override
    public ApiResponse<TransferResponse> create(CreateTransferRequest req) {
        Attributes attrs = Attributes.builder()
                .put("transferFrom", req.getTransferFrom() != null ? req.getTransferFrom() : "")
                .put("transferTo", req.getTransferTo() != null ? req.getTransferTo() : "")
                .build();

        return tracingMetrics.traceAndMeasure("createTransfer", "create_transfer", attrs, () -> {
            log.info("🚀 Starting create transfer: {}", req);

            if (!cardExists(req.getTransferFrom())) {
                log.error("❌ sender card {} not found", req.getTransferFrom());
                return ApiResponse.<TransferResponse>builder()
                        .status("error")
                        .message("Sender card not found")
                        .build();
            }
            if (!cardExists(req.getTransferTo())) {
                log.error("❌ receiver card {} not found", req.getTransferTo());
                return ApiResponse.<TransferResponse>builder()
                        .status("error")
                        .message("Receiver card not found")
                        .build();
            }

            Saldo senderSaldo = findSaldo(req.getTransferFrom(), "Sender saldo not found");
            Saldo receiverSaldo = findSaldo(req.getTransferTo(), "Receiver saldo not found");

            if (senderSaldo.totalBalance() < req.getTransferAmount()) {
                log.error("❌ insufficient balance, requested={}, available={}",
                        req.getTransferAmount(), senderSaldo.totalBalance());
                return ApiResponse.<TransferResponse>builder()
                        .status("error")
                        .message("Insufficient balance")
                        .build();
            }

            Long senderBalance = (long) senderSaldo.totalBalance();
            Long receiverBalance = (long) receiverSaldo.totalBalance();

            try {
                senderBalance = senderBalance - req.getTransferAmount();
                updateSaldoBalance(senderSaldo.cardNumber(), senderBalance);

                receiverBalance = receiverBalance + req.getTransferAmount();
                updateSaldoBalance(receiverSaldo.cardNumber(), receiverBalance);

                Transfer transferEntity = new Transfer();

                UUID transferNo = UUID.randomUUID();

                transferEntity.setTransferNo(transferNo.toString());
                transferEntity.setTransferFrom(req.getTransferFrom());
                transferEntity.setTransferAmount(req.getTransferAmount());
                transferEntity.setTransferTo(req.getTransferTo());
                transferEntity.setTransferTime(LocalDateTime.now());
                transferEntity.setStatus(Status.PENDING);

                Transfer transfer = transferCommandRepository.save(transferEntity);

                transferCommandRepository.updateTransferStatus(transfer.getTransferId(), Status.SUCCESS.toString());

                log.info("✅ Transfer created successfully with ID={}", transfer.getTransferId());
                enqueueTransferEmail(transfer, transfer.getTransferId());
                return ApiResponse.<TransferResponse>builder()
                        .status("success")
                        .message("Transfer created successfully")
                        .data(TransferResponse.from(transfer))
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to create transfer, rolling back balances", e);

                try {
                    senderBalance = senderBalance + req.getTransferAmount();
                    updateSaldoBalance(senderSaldo.cardNumber(), senderBalance);
                } catch (Exception rb) {
                    log.error("⚠️ Failed to rollback sender saldo", rb);
                }
                try {
                    receiverBalance = receiverBalance - req.getTransferAmount();
                    updateSaldoBalance(receiverSaldo.cardNumber(), receiverBalance);
                } catch (Exception rb) {
                    log.error("⚠️ Failed to rollback receiver saldo", rb);
                }
                return ApiResponse.<TransferResponse>builder()
                        .status("error")
                        .message("Failed to create transfer")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<TransferResponse> update(UpdateTransferRequest req) {
        Attributes attrs = Attributes.builder()
                .put("transferId", String.valueOf(req.getTransferId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateTransfer", "update_transfer", attrs, () -> {
            log.info("🔄 Starting update transaction process: {}", req);

            if (req.getTransferId() == null || req.getTransferId().isBlank()) {
                throw new IllegalArgumentException("transfer_id is required");
            }

            var transfer = transferQueryRepository.findTransferById(req.getTransferId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Transfer " + req.getTransferId() + " not found"));

            Long amountDifference = req.getTransferAmount() - transfer.getTransferAmount();

            try {
                Saldo senderSaldo = findSaldo(transfer.getTransferFrom(),
                        "Sender card " + transfer.getTransferFrom() + " not found");

                Long newSenderBalance = (long) senderSaldo.totalBalance() - amountDifference;
                if (newSenderBalance < 0) {
                    log.error("❌ Insufficient balance for sender {}", transfer.getTransferFrom());
                    transferCommandRepository.updateTransferStatus(req.getTransferId(),
                            Status.FAILED.toString());
                    throw new IllegalStateException("Insufficient balance");
                }

                updateSaldoBalance(senderSaldo.cardNumber(), newSenderBalance);

                Saldo receiverSaldo;
                try {
                    receiverSaldo = findSaldo(transfer.getTransferTo(),
                            "Receiver card " + transfer.getTransferTo() + " not found");
                } catch (ResourceNotFoundException ex) {
                    rollbackSenderSaldo(senderSaldo.cardNumber(), newSenderBalance, amountDifference);
                    transferCommandRepository.updateTransferStatus(req.getTransferId(),
                            Status.FAILED.toString());
                    throw ex;
                }

                Long newReceiverBalance = (long) receiverSaldo.totalBalance() + amountDifference;
                try {
                    updateSaldoBalance(receiverSaldo.cardNumber(), newReceiverBalance);
                } catch (Exception e) {
                    log.error("❌ Failed to update receiver saldo, rolling back..");
                    rollbackSenderSaldo(senderSaldo.cardNumber(), newSenderBalance, amountDifference);
                    rollbackReceiverSaldo(receiverSaldo.cardNumber(), newReceiverBalance, amountDifference);
                    transferCommandRepository.updateTransferStatus(req.getTransferId(),
                            Status.FAILED.toString());
                    return ApiResponse.<TransferResponse>builder()
                            .status("error")
                            .message("Failed to update transfer")
                            .build();
                }

                transfer.setTransferAmount(req.getTransferAmount());
                transfer.setTransferFrom(req.getTransferFrom());
                transfer.setTransferTo(req.getTransferTo());
                var updatedTransfer = transferCommandRepository.save(transfer);

                transferCommandRepository.updateTransferStatus(req.getTransferId(), Status.SUCCESS.toString());

                log.info("✅ Successfully updated transfer {}", req.getTransferId());

                return ApiResponse.<TransferResponse>builder()
                        .status("success")
                        .message("Transfer updated successfully")
                        .data(TransferResponse.from(updatedTransfer))
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to update transfer {}", req.getTransferId(), e);
                return ApiResponse.<TransferResponse>builder()
                        .status("error")
                        .message("Failed to update transfer")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<TransferResponseDeleteAt> trashed(String transferId) {
        Attributes attrs = Attributes.builder()
                .put("transferId", String.valueOf(transferId))
                .build();

        return tracingMetrics.traceAndMeasure("trashedTransfer", "trashed_transfer", attrs, () -> {
            log.info("🗑️ Trashing transfer id={}", transferId);

            try {
                Transfer transfer = transferCommandRepository.trashed(transferId);
                return ApiResponse.<TransferResponseDeleteAt>builder()
                        .status("success")
                        .message("🗑️ Transfer trashed successfully!")
                        .data(TransferResponseDeleteAt.from(transfer))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash transfer id={}", transferId, e);
                return ApiResponse.<TransferResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash transfer")
                        .build();

            }
        });
    }

    @Override
    public ApiResponse<TransferResponseDeleteAt> restore(String transferId) {
        Attributes attrs = Attributes.builder()
                .put("transferId", String.valueOf(transferId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreTransfer", "restore_transfer", attrs, () -> {
            log.info("♻️ Restoring transfer id={}", transferId);

            try {
                Transfer transfer = transferCommandRepository.restore(transferId);
                return ApiResponse.<TransferResponseDeleteAt>builder()
                        .status("success")
                        .message("♻️ Transfer restored successfully!")
                        .data(TransferResponseDeleteAt.from(transfer))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore transfer id={}", transferId, e);
                return ApiResponse.<TransferResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore transfer")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(String transferId) {
        Attributes attrs = Attributes.builder()
                .put("transferId", String.valueOf(transferId))
                .build();

        return tracingMetrics.traceAndMeasure("deletePermanentTransfer", "delete_permanent_transfer", attrs, () -> {
            log.info("🧨 Permanently deleting transfer id={}", transferId);

            try {
                transferCommandRepository.deletePermanent(transferId);
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🧨 Transfer permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete transfer id={}", transferId, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("💥 Failed to permanently delete transfer")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllTransfers", "restore_all_transfers", () -> {
            log.info("🔄 Restoring ALL trashed transfers");

            try {
                transferCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("🔄 All transfers restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all transfers", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("💥 Failed to restore all transfers")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllTransfers", "delete_all_transfers", () -> {
            log.info("💣 Permanently deleting ALL trashed transfers");

            try {
                transferCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("💣 All transfers permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all transfers", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("💥 Failed to delete all transfers")
                        .data(false)
                        .build();
            }
        });
    }

    private boolean cardExists(String cardNumber) {
        try {
            cardPort.findCardByCardNumber(cardNumber);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }

    private Saldo findSaldo(String cardNumber, String notFoundMessage) {
        try {
            return saldoPort.findByCardNumber(cardNumber);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(notFoundMessage);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new ResourceNotFoundException(notFoundMessage);
            }
            throw e;
        }
    }

    private void updateSaldoBalance(String cardNumber, Long newBalance) {
        saldoPort.updateSaldoBalance(new SaldoPort.BalanceUpdate(
                cardNumber, newBalance.intValue(), null, null, null, null, null));
    }

    private void rollbackSenderSaldo(String cardNumber, Long currentBalance, Long amountDifference) {
        try {
            updateSaldoBalance(cardNumber, currentBalance + amountDifference);
        } catch (Exception e) {
            log.error("⚠️ Rollback sender saldo failed: {}", e.getMessage());
        }
    }

    private void rollbackReceiverSaldo(String cardNumber, Long currentBalance, Long amountDifference) {
        try {
            updateSaldoBalance(cardNumber, currentBalance - amountDifference);
        } catch (Exception e) {
            log.error("⚠️ Rollback receiver saldo failed: {}", e.getMessage());
        }
    }

    /**
     * Enqueues a transfer-success email to the configured notification address.
     *
     * <p>This port's {@code Card} carries no email, so the recipient is a
     * configured notification inbox rather than the cardholder — topic and
     * envelope still match the email-service contract.
     */
    private void enqueueTransferEmail(Transfer transfer, String transferId) {
        try {
            String subject = "Transfer Successful - SanEdge";
            String body = String.format(
                    "Hello,\n\nYour transfer of %d has been processed successfully.\n\nRegards,\nSupport Team",
                    transfer.getTransferAmount());
            OutboxEventFactory.Envelope envelope = OutboxEventFactory.email(
                    TOPIC_TRANSFER_CREATE, notificationEmail, subject, body);
            Outbox event = new Outbox();
            event.setAggregateType("TRANSFER");
            event.setAggregateId(String.valueOf(transferId));
            event.setTopic(TOPIC_TRANSFER_CREATE);
            event.setPayload(envelope.payload());
            outboxRepository.enqueue(event);
        } catch (RuntimeException e) {
            log.warn("⚠️ Failed to enqueue transfer email: {}", e.getMessage());
        }
    }

}
