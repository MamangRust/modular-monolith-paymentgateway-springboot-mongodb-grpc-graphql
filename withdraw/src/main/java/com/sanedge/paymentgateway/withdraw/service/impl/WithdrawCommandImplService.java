package com.sanedge.paymentgateway.withdraw.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.adapter.card.CardPort;
import com.sanedge.paymentgateway.common.adapter.model.Card;
import com.sanedge.paymentgateway.common.adapter.model.Saldo;
import com.sanedge.paymentgateway.common.adapter.saldo.SaldoPort;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.enums.Status;
import com.sanedge.paymentgateway.common.exception.ResourceNotFoundException;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.common.outbox.Outbox;
import com.sanedge.paymentgateway.common.outbox.OutboxEventFactory;
import com.sanedge.paymentgateway.common.outbox.OutboxRepository;
import com.sanedge.paymentgateway.withdraw.domain.requests.CreateWithdrawRequest;
import com.sanedge.paymentgateway.withdraw.domain.requests.UpdateWithdrawRequest;
import com.sanedge.paymentgateway.withdraw.domain.responses.WithdrawResponse;
import com.sanedge.paymentgateway.withdraw.domain.responses.WithdrawResponseDeleteAt;
import com.sanedge.paymentgateway.withdraw.models.Withdraw;
import com.sanedge.paymentgateway.withdraw.repository.WithdrawCommandRepository;
import com.sanedge.paymentgateway.withdraw.repository.WithdrawQueryRepository;
import com.sanedge.paymentgateway.withdraw.service.WithdrawCommandService;

import io.grpc.StatusRuntimeException;
import io.opentelemetry.api.common.Attributes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawCommandImplService implements WithdrawCommandService {

    private static final String TOPIC_WITHDRAW_CREATE = "email-service-topic-withdraw-create";

    private final WithdrawQueryRepository withdrawQueryRepository;
    private final WithdrawCommandRepository withdrawCommandRepository;
    private final CardPort cardPort;
    private final SaldoPort saldoPort;
    private final Validator validator;
    private final TracingMetrics tracingMetrics;
    private final OutboxRepository outboxRepository;

    @Value("${notification.withdraw.email:admin@example.com}")
    private String notificationEmail;

    @Override
    public ApiResponse<WithdrawResponse> create(CreateWithdrawRequest req) {
        Attributes attrs = Attributes.builder()
                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                .build();

        return tracingMetrics.traceAndMeasure("createWithdraw", "create", attrs, () -> {
            try {
                validateRequest(req);

                if (req.getWithdrawAmount() == null || req.getWithdrawAmount() <= 0) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("withdraw_amount must be > 0")
                            .data(null)
                            .build();
                }

                Card card = findCard(req.getCardNumber());
                if (card == null) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Card not found")
                            .data(null)
                            .build();
                }

                Saldo saldo = findSaldo(req.getCardNumber());
                if (saldo == null) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Saldo not found")
                            .data(null)
                            .build();
                }

                if (saldo.totalBalance() < req.getWithdrawAmount()) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Insufficient balance")
                            .data(null)
                            .build();
                }

                saldoPort.updateSaldoWithdraw(new SaldoPort.WithdrawUpdate(
                        saldo.cardNumber(), 0, null, req.getWithdrawAmount().intValue(), null, null, null));

                Withdraw withdraw = new Withdraw();
                withdraw.setCardNumber(req.getCardNumber());
                withdraw.setWithdrawNo(UUID.randomUUID().toString());
                withdraw.setStatus(Status.PENDING);
                withdraw.setWithdrawTime(req.getWithdrawTime() != null ? req.getWithdrawTime()
                        : LocalDateTime.now());
                withdraw.setWithdrawAmount(req.getWithdrawAmount());

                Withdraw created = withdrawCommandRepository.save(withdraw);

                Withdraw updated = withdrawCommandRepository.updateStatus(created.getWithdrawId(),
                        Status.SUCCESS.toString());

                enqueueWithdrawEmail(updated, updated.getWithdrawId());

                return ApiResponse.<WithdrawResponse>builder()
                        .status("success")
                        .message("Created withdraw successfully")
                        .data(WithdrawResponse.from(updated))
                        .build();

            } catch (Exception e) {
                log.error("💥 Error creating withdraw", e);
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Failed to create withdraw")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<WithdrawResponse> update(UpdateWithdrawRequest req) {
        Attributes attrs = Attributes.builder()
                .put("withdrawId", String.valueOf(req.getWithdrawId()))
                .build();

        return tracingMetrics.traceAndMeasure("updateWithdraw", "update", attrs, () -> {
            try {
                validateRequest(req);

                if (req.getWithdrawId() == null) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("withdraw_id is required")
                            .data(null)
                            .build();
                }

                if (req.getWithdrawAmount() == null || req.getWithdrawAmount() <= 0) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("withdraw_amount must be > 0")
                            .data(null)
                            .build();
                }

                Card card = findCard(req.getCardNumber());
                if (card == null) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Card not found")
                            .data(null)
                            .build();
                }

                var withdrawOpt = withdrawQueryRepository.findById(req.getWithdrawId());
                if (withdrawOpt.isEmpty()) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Withdraw not found")
                            .data(null)
                            .build();
                }

                Saldo saldo = findSaldo(req.getCardNumber());
                if (saldo == null) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Saldo not found")
                            .data(null)
                            .build();
                }

                if (saldo.totalBalance() < req.getWithdrawAmount()) {
                    return ApiResponse.<WithdrawResponse>builder()
                            .status("error")
                            .message("Insufficient balance")
                            .data(null)
                            .build();
                }

                saldoPort.updateSaldoWithdraw(new SaldoPort.WithdrawUpdate(
                        saldo.cardNumber(), 0, null, req.getWithdrawAmount().intValue(), null, null, null));

                var withdraw = withdrawOpt.get();
                withdraw.setCardNumber(req.getCardNumber());
                withdraw.setWithdrawAmount(req.getWithdrawAmount());
                withdraw.setWithdrawTime(req.getWithdrawTime() != null ? req.getWithdrawTime()
                        : LocalDateTime.now());

                Withdraw updated = withdrawCommandRepository.save(withdraw);
                Withdraw refreshed = withdrawCommandRepository.updateStatus(updated.getWithdrawId(),
                        Status.SUCCESS.toString());

                return ApiResponse.<WithdrawResponse>builder()
                        .status("success")
                        .message("Updated withdraw successfully")
                        .data(WithdrawResponse.from(refreshed))
                        .build();

            } catch (Exception e) {
                log.error("💥 Error updating withdraw", e);
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Failed to update withdraw")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<WithdrawResponseDeleteAt> trashed(String withdrawId) {
        Attributes attrs = Attributes.builder()
                .put("withdrawId", String.valueOf(withdrawId))
                .build();

        return tracingMetrics.traceAndMeasure("trashedWithdraw", "trashed_withdraw", attrs, () -> {
            try {
                Withdraw withdraw = withdrawCommandRepository.trashed(withdrawId);
                return ApiResponse.<WithdrawResponseDeleteAt>builder()
                        .status("success")
                        .message("Withdraw trashed successfully!")
                        .data(WithdrawResponseDeleteAt.from(withdraw))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to trash withdraw id={}", withdrawId, e);
                return ApiResponse.<WithdrawResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to trash withdraw")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<WithdrawResponseDeleteAt> restore(String withdrawId) {
        Attributes attrs = Attributes.builder()
                .put("withdrawId", String.valueOf(withdrawId))
                .build();

        return tracingMetrics.traceAndMeasure("restoreWithdraw", "restore_withdraw", attrs, () -> {
            try {
                Withdraw withdraw = withdrawCommandRepository.restore(withdrawId);
                return ApiResponse.<WithdrawResponseDeleteAt>builder()
                        .status("success")
                        .message("Withdraw restored successfully!")
                        .data(WithdrawResponseDeleteAt.from(withdraw))
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore withdraw id={}", withdrawId, e);
                return ApiResponse.<WithdrawResponseDeleteAt>builder()
                        .status("error")
                        .message("Failed to restore withdraw")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(String withdrawId) {
        Attributes attrs = Attributes.builder()
                .put("withdrawId", String.valueOf(withdrawId))
                .build();

        return tracingMetrics.traceAndMeasure("deletePermanentWithdraw", "delete_permanent_withdraw", attrs, () -> {
            try {
                withdrawCommandRepository.deletePermanent(withdrawId);
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("Withdraw permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to permanently delete withdraw id={}", withdrawId, e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to permanently delete withdraw")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        return tracingMetrics.traceAndMeasure("restoreAllWithdraws", "restore_all_withdraws", () -> {
            try {
                withdrawCommandRepository.restoreAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All withdraws restored successfully!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to restore all withdraws", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to restore all withdraws")
                        .data(false)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        return tracingMetrics.traceAndMeasure("deleteAllWithdraws", "delete_all_withdraws", () -> {
            try {
                withdrawCommandRepository.deleteAllDeleted();
                return ApiResponse.<Boolean>builder()
                        .status("success")
                        .message("All withdraws permanently deleted!")
                        .data(true)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to delete all withdraws", e);
                return ApiResponse.<Boolean>builder()
                        .status("error")
                        .message("Failed to delete all withdraws")
                        .data(false)
                        .build();
            }
        });
    }

    private Card findCard(String cardNumber) {
        try {
            return cardPort.findCardByCardNumber(cardNumber);
        } catch (ResourceNotFoundException e) {
            return null;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    private Saldo findSaldo(String cardNumber) {
        try {
            return saldoPort.findByCardNumber(cardNumber);
        } catch (ResourceNotFoundException e) {
            return null;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }

    /**
     * Enqueues a withdraw-success email to the configured notification address.
     *
     * <p>This port's {@code Card} carries no email, so the recipient is a
     * configured notification inbox rather than the cardholder — topic and
     * envelope still match the email-service contract.
     */
    private void enqueueWithdrawEmail(Withdraw withdraw, String withdrawId) {
        try {
            String subject = "Withdraw Successful - SanEdge";
            String body = String.format(
                    "Hello,\n\nYour withdraw of %d has been processed successfully.\n\nRegards,\nSupport Team",
                    withdraw.getWithdrawAmount());
            OutboxEventFactory.Envelope envelope = OutboxEventFactory.email(
                    TOPIC_WITHDRAW_CREATE, notificationEmail, subject, body);
            Outbox event = new Outbox();
            event.setAggregateType("WITHDRAW");
            event.setAggregateId(String.valueOf(withdrawId));
            event.setTopic(TOPIC_WITHDRAW_CREATE);
            event.setPayload(envelope.payload());
            outboxRepository.enqueue(event);
        } catch (RuntimeException e) {
            log.warn("⚠️ Failed to enqueue withdraw email: {}", e.getMessage());
        }
    }
}
