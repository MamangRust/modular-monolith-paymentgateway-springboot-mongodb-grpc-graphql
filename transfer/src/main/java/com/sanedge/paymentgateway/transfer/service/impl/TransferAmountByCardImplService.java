package com.sanedge.paymentgateway.transfer.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthYearCardNumber;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse;
import com.sanedge.paymentgateway.transfer.models.TransferMonthAmount;
import com.sanedge.paymentgateway.transfer.models.TransferYearAmount;
import com.sanedge.paymentgateway.transfer.repository.statsbycard.TransferAmountByCardRepository;
import com.sanedge.paymentgateway.transfer.service.TransferAmountByCardService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferAmountByCardImplService implements TransferAmountByCardService {
        private final TransferAmountByCardRepository transferAmountByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmountsBySender(MonthYearCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthlyAmountsBySender", "find_monthly_amounts_by_sender", attrs, () -> {
                        String cardNumber = req.getCardNumber();
                        Long year = (long) req.getYear();

                        log.info("📊 Fetching monthly transfer amounts (SENDER) for year={}, cardNumber={}",
                                        year, cardNumber);

                        validateYearAndCard(year, cardNumber);

                        try {
                                List<TransferMonthAmount> amounts = transferAmountByCardRepository
                                                .findMonthlyTransferAmountsBySender(cardNumber, year);

                                log.info("✅ Retrieved {} monthly transfer amount records for sender card={} in year={}",
                                                amounts.size(), cardNumber, year);

                                List<TransferMonthAmountResponse> response = amounts.stream()
                                                .map(TransferMonthAmountResponse::from)
                                                .toList();

                                return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                                                .status("success")
                                                .message("Monthly transfer amounts (sender) retrieved successfully!")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly transfer amounts (sender) for year={}, cardNumber={}",
                                                req.getYear(), req.getCardNumber(), e);
                                return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                                                .status("failed")
                                                .message("Failed to fetch monthly transfer amounts (sender)")
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmountsByReceiver(MonthYearCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthlyAmountsByReceiver", "find_monthly_amounts_by_receiver", attrs, () -> {
                        String cardNumber = req.getCardNumber();
                        Long year = (long) req.getYear();

                        log.info("📊 Fetching monthly transfer amounts (RECEIVER) for year={}, cardNumber={}",
                                        year, cardNumber);

                        validateYearAndCard(year, cardNumber);

                        try {
                                List<TransferMonthAmount> amounts = transferAmountByCardRepository
                                                .findMonthlyTransferAmountsByReceiver(cardNumber, year);

                                log.info("✅ Retrieved {} monthly transfer amount records for receiver card={} in year={}",
                                                amounts.size(), cardNumber, year);

                                List<TransferMonthAmountResponse> response = amounts.stream()
                                                .map(TransferMonthAmountResponse::from)
                                                .toList();

                                return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                                                .status("success")
                                                .message("Monthly transfer amounts (receiver) retrieved successfully!")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly transfer amounts (receiver) for year={}, cardNumber={}",
                                                req.getYear(), req.getCardNumber(), e);
                                return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                                                .status("failed")
                                                .message("Failed to fetch monthly transfer amounts (receiver)")
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<TransferYearAmountResponse>> findYearlyAmountsBySender(MonthYearCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyAmountsBySender", "find_yearly_amounts_by_sender", attrs, () -> {
                        String cardNumber = req.getCardNumber();
                        Long year = (long) req.getYear();

                        log.info("📊 Fetching yearly transfer amounts (SENDER) until year={}, cardNumber={}",
                                        year, cardNumber);

                        validateYearAndCard(year, cardNumber);

                        try {
                                List<TransferYearAmount> amounts = transferAmountByCardRepository
                                                .findYearlyTransferAmountsBySender(cardNumber, year);

                                log.info("✅ Retrieved {} yearly transfer amount records for sender card={} until year={}",
                                                amounts.size(), cardNumber, year);

                                List<TransferYearAmountResponse> response = amounts.stream()
                                                .map(TransferYearAmountResponse::from)
                                                .toList();

                                return ApiResponse.<List<TransferYearAmountResponse>>builder()
                                                .status("success")
                                                .message("Yearly transfer amounts (sender) retrieved successfully!")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly transfer amounts (sender) until year={}, cardNumber={}",
                                                req.getYear(), req.getCardNumber(), e);
                                return ApiResponse.<List<TransferYearAmountResponse>>builder()
                                                .status("failed")
                                                .data(null)
                                                .message("Failed to fetch yearly transfer amounts (sender)")
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<TransferYearAmountResponse>> findYearlyAmountsByReceiver(MonthYearCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyAmountsByReceiver", "find_yearly_amounts_by_receiver", attrs, () -> {
                        String cardNumber = req.getCardNumber();
                        Long year = (long) req.getYear();

                        log.info("📊 Fetching yearly transfer amounts (RECEIVER) until year={}, cardNumber={}",
                                        year, cardNumber);

                        validateYearAndCard(year, cardNumber);

                        try {
                                List<TransferYearAmount> amounts = transferAmountByCardRepository
                                                .findYearlyTransferAmountsByReceiver(cardNumber, year);

                                log.info("✅ Retrieved {} yearly transfer amount records for receiver card={} until year={}",
                                                amounts.size(), cardNumber, year);

                                List<TransferYearAmountResponse> response = amounts.stream()
                                                .map(TransferYearAmountResponse::from)
                                                .toList();

                                return ApiResponse.<List<TransferYearAmountResponse>>builder()
                                                .status("success")
                                                .message("Yearly transfer amounts (receiver) retrieved successfully!")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly transfer amounts (receiver) until year={}, cardNumber={}",
                                                req.getYear(), req.getCardNumber(), e);
                                return ApiResponse.<List<TransferYearAmountResponse>>builder()
                                                .status("failed")
                                                .message("Failed to fetch yearly transfer amounts (receiver)")
                                                .build();
                        }
                });
        }

        private void validateYearAndCard(Long year, String cardNumber) {
                if (year < 1 || year > 9999) {
                        log.error("❌ Invalid year provided: {}", year);
                        throw new IllegalArgumentException("Invalid year");
                }
                if (cardNumber == null || cardNumber.isBlank()) {
                        log.error("❌ Invalid card number provided: {}", cardNumber);
                        throw new IllegalArgumentException("Invalid card number");
                }
        }
}
