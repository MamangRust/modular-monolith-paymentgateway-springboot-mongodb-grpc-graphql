package com.sanedge.paymentgateway.transaction.service.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthStatusTransactionCardNumber;
import com.sanedge.paymentgateway.transaction.domain.requests.YearStatusTransactionCardNumber;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusFailed;
import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusSuccess;
import com.sanedge.paymentgateway.transaction.repository.TransactionStatusByCardRepository;
import com.sanedge.paymentgateway.transaction.service.TransactionStatusByCardService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionStatusByCardImplService implements TransactionStatusByCardService {
    private final TransactionStatusByCardRepository transactionStatusByCardRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TransactionResponseMonthStatusSuccess>> findMonthStatusSuccess(
            MonthStatusTransactionCardNumber req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();
        int month = req.getMonth();

        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .put("month", String.valueOf(month))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthStatusSuccessByCard", "find_month_status_success", attrs, () -> {
            log.info("📊 Fetching monthly SUCCESS transactions for cardNumber={}, year={}, month={}", cardNumber,
                    year, month);

            if (!isValidYearMonth(year, month) || !isValidCard(cardNumber)) {
                log.error("❌ Invalid input: year={}, month={}, cardNumber={}", year, month, cardNumber);
                return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Invalid input - year, month, cardNumber")
                        .build();
            }

            try {
                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                LocalDate nextMonth = currentMonth.plusMonths(1);

                List<TransactionMonthStatusSuccess> results = transactionStatusByCardRepository
                        .findMonthTransactionStatusSuccessByCard(cardNumber, year, month,
                                (long) nextMonth.getYear(), nextMonth.getMonthValue());

                List<TransactionResponseMonthStatusSuccess> response = results.stream()
                        .map(TransactionResponseMonthStatusSuccess::from)
                        .toList();

                log.info("✅ Found {} monthly SUCCESS transaction records for cardNumber={}, year={}, month={}",
                        results.size(), cardNumber, year, month);

                return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + results.size() + " monthly SUCCESS transaction records")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly SUCCESS transactions for cardNumber={}, year={}, month={}",
                        cardNumber, year, month, e);
                return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly SUCCESS transactions")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionResponseYearStatusSuccess>> findYearlyStatusSuccess(
            YearStatusTransactionCardNumber req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyStatusSuccessByCard", "find_yearly_status_success", attrs, () -> {
            log.info("📊 Fetching yearly SUCCESS transactions for cardNumber={}, year={}", cardNumber, year);

            if (!isValidYear(year) || !isValidCard(cardNumber)) {
                log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
                return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Invalid input - year, cardNumber")
                        .build();
            }

            try {
                List<TransactionYearStatusSuccess> results = transactionStatusByCardRepository
                        .findYearlyTransactionStatusSuccessByCard(cardNumber, year);

                List<TransactionResponseYearStatusSuccess> response = results.stream()
                        .map(TransactionResponseYearStatusSuccess::from)
                        .toList();

                log.info("✅ Found {} yearly SUCCESS transaction records for cardNumber={}, year={}",
                        results.size(), cardNumber, year);

                return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + results.size() + " yearly SUCCESS transaction records")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly SUCCESS transactions for cardNumber={}, year={}",
                        cardNumber, year, e);
                return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                        .status("error")
                        .message("Failed to fetch yearly SUCCESS transactions")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionResponseMonthStatusFailed>> findMonthStatusFailed(
            MonthStatusTransactionCardNumber req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();
        int month = req.getMonth();

        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .put("month", String.valueOf(month))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthStatusFailedByCard", "find_month_status_failed", attrs, () -> {
            log.info("📊 Fetching monthly FAILED transactions for cardNumber={}, year={}, month={}", cardNumber,
                    year, month);

            if (!isValidYearMonth(year, month) || !isValidCard(cardNumber)) {
                log.error("❌ Invalid input: year={}, month={}, cardNumber={}", year, month, cardNumber);
                return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Invalid input - year, month, cardNumber")
                        .build();
            }

            try {
                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                LocalDate nextMonth = currentMonth.plusMonths(1);

                List<TransactionMonthStatusFailed> results = transactionStatusByCardRepository
                        .findMonthTransactionStatusFailedByCard(cardNumber, year, month,
                                (long) nextMonth.getYear(), nextMonth.getMonthValue());

                List<TransactionResponseMonthStatusFailed> response = results.stream()
                        .map(TransactionResponseMonthStatusFailed::from)
                        .toList();

                log.info("✅ Found {} monthly FAILED transaction records for cardNumber={}, year={}, month={}",
                        results.size(), cardNumber, year, month);

                return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                        .status("success")
                        .message("Found " + results.size() + " monthly FAILED transaction records")
                        .data(response)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly FAILED transactions for cardNumber={}, year={}, month={}",
                        cardNumber, year, month, e);
                return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                        .status("error")
                        .message("Failed to fetch monthly FAILED transactions")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionResponseYearStatusFailed>> findYearlyStatusFailed(
            YearStatusTransactionCardNumber req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyStatusFailedByCard", "find_yearly_status_failed", attrs, () -> {
            log.info("📊 Fetching yearly FAILED transactions for cardNumber={}, year={}", cardNumber, year);

            if (!isValidYear(year) || !isValidCard(cardNumber)) {
                log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
                return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                        .status("error")
                        .message("Invalid input - year, cardNumber")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionYearStatusFailed> results = transactionStatusByCardRepository
                        .findYearlyTransactionStatusFailedByCard(cardNumber, year);

                List<TransactionResponseYearStatusFailed> response = results.stream()
                        .map(TransactionResponseYearStatusFailed::from)
                        .toList();

                log.info("✅ Found {} yearly FAILED transaction records for cardNumber={}, year={}",
                        results.size(), cardNumber, year);

                return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + results.size() + " yearly FAILED transaction records")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly FAILED transactions for cardNumber={}, year={}",
                        cardNumber, year, e);
                return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly FAILED transactions")
                        .build();
            }
        });
    }

    private boolean isValidYearMonth(Long year, Integer month) {
        return year != null && year >= 1 && year <= 9999 && month != null && month >= 1 && month <= 12;
    }

    private boolean isValidYear(Long year) {
        return year != null && year >= 1 && year <= 9999;
    }

    private boolean isValidCard(String cardNumber) {
        return cardNumber != null && !cardNumber.isBlank();
    }
}
