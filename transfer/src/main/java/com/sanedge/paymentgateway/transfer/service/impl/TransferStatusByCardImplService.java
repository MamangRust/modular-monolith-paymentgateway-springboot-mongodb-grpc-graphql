package com.sanedge.paymentgateway.transfer.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthStatusTransferCardNumber;
import com.sanedge.paymentgateway.transfer.domain.requests.YearStatusTransferCardNumber;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess;
import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.models.TransferYearStatusFailed;
import com.sanedge.paymentgateway.transfer.models.TransferYearStatusSuccess;
import com.sanedge.paymentgateway.transfer.repository.statsbycard.TransferStatusByCardRepository;
import com.sanedge.paymentgateway.transfer.service.TransferStatusByCardService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferStatusByCardImplService implements TransferStatusByCardService {
        private final TransferStatusByCardRepository transferStatusByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<TransferResponseMonthStatusSuccess>> findMonthStatusSuccessByCard(
                        MonthStatusTransferCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthStatusSuccessByCard", "find_month_status_success_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();
                                int month = req.getMonth();

                                log.info("📊 Fetching monthly SUCCESS transfer status for card: {}, year: {}, month: {}",
                                                cardNumber, year, month);

                                validateYearMonth(year, month);
                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                log.debug("🗓️ Querying data for: {}/{} and {}/{}",
                                                year, month,
                                                (long) nextMonth.getYear(), nextMonth.getMonthValue());

                                List<TransferMonthStatusSuccess> results = transferStatusByCardRepository
                                                .findMonthTransferStatusSuccessByCard(cardNumber, year, month,
                                                                (long) nextMonth.getYear(),
                                                                nextMonth.getMonthValue());

                                List<TransferResponseMonthStatusSuccess> response = results.stream()
                                                .map(TransferResponseMonthStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseMonthStatusSuccess>>builder()
                                                .status("success")
                                                .message("Monthly SUCCESS transfer status by card fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                return ApiResponse.<List<TransferResponseMonthStatusSuccess>>builder()
                                                .status("error")
                                                .message("Monthly SUCCESS transfer status by card fetching failed")
                                                .build();
                        }
                });

        }

        @Override
        public ApiResponse<List<TransferResponseYearStatusSuccess>> findYearlyStatusSuccessByCard(
                        YearStatusTransferCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyStatusSuccessByCard", "find_yearly_status_success_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();

                                log.info("📊 Fetching yearly SUCCESS transfer status for card: {}, year: {}", cardNumber, year);

                                validateYear(year);

                                List<TransferYearStatusSuccess> results = transferStatusByCardRepository
                                                .findYearlyTransferStatusSuccessByCard(cardNumber, year);

                                List<TransferResponseYearStatusSuccess> response = results.stream()
                                                .map(TransferResponseYearStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseYearStatusSuccess>>builder()
                                                .status("success")
                                                .message("Yearly SUCCESS transfer status by card fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                return ApiResponse.<List<TransferResponseYearStatusSuccess>>builder()
                                                .status("error")
                                                .message("Yearly SUCCESS transfer status by card fetching failed")
                                                .build();
                        }
                });

        }

        @Override
        public ApiResponse<List<TransferResponseMonthStatusFailed>> findMonthStatusFailedByCard(
                        MonthStatusTransferCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthStatusFailedByCard", "find_month_status_failed_by_card", attrs, () -> {
                        try {

                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();
                                int month = req.getMonth();

                                log.info("📊 Fetching monthly FAILED transfer status for card: {}, year: {}, month: {}",
                                                cardNumber, year, month);

                                validateYearMonth(year, month);

                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                log.debug("🗓️ Querying data for: {}/{} and {}/{}",
                                                year, month,
                                                (long) nextMonth.getYear(), nextMonth.getMonthValue());

                                List<TransferMonthStatusFailed> results = transferStatusByCardRepository
                                                .findMonthTransferStatusFailedByCard(cardNumber, year, month,
                                                                (long) nextMonth.getYear(),
                                                                nextMonth.getMonthValue());

                                List<TransferResponseMonthStatusFailed> response = results.stream()
                                                .map(TransferResponseMonthStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseMonthStatusFailed>>builder()
                                                .status("success")
                                                .message("Monthly FAILED transfer status by card fetched successfully")
                                                .data(response)
                                                .build();
                        } catch (Exception e) {
                                return ApiResponse.<List<TransferResponseMonthStatusFailed>>builder()
                                                .status("error")
                                                .message("Monthly FAILED transfer status by card fetching failed")
                                                .build();
                        }
                });

        }

        @Override
        public ApiResponse<List<TransferResponseYearStatusFailed>> findYearlyStatusFailedByCard(
                        YearStatusTransferCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyStatusFailedByCard", "find_yearly_status_failed_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();

                                log.info("📊 Fetching yearly FAILED transfer status for card: {}, year: {}", cardNumber, year);

                                validateYear(year);

                                List<TransferYearStatusFailed> results = transferStatusByCardRepository
                                                .findYearlyTransferStatusFailedByCard(cardNumber, year);

                                List<TransferResponseYearStatusFailed> response = results.stream()
                                                .map(TransferResponseYearStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseYearStatusFailed>>builder()
                                                .status("success")
                                                .message("Yearly FAILED transfer status by card fetched successfully")
                                                .data(response)
                                                .build();
                        } catch (Exception e) {
                                return ApiResponse.<List<TransferResponseYearStatusFailed>>builder()
                                                .status("error")
                                                .message("Yearly FAILED transfer status by card fetching failed")
                                                .build();
                        }
                });

        }

        private void validateYearMonth(Long year, Integer month) {
                if (year < 1 || year > 9999 || month < 1 || month > 12) {
                        log.error("❌ Invalid year/month: year={}, month={}", year, month);
                        throw new IllegalArgumentException("❌ Invalid year or month");
                }
        }

        private void validateYear(Long year) {
                if (year < 1 || year > 9999) {
                        log.error("❌ Invalid year: {}", year);
                        throw new IllegalArgumentException("❌ Invalid year");
                }
        }
}
