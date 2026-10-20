package com.sanedge.paymentgateway.transaction.service.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthStatusTransaction;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusFailed;
import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusSuccess;
import com.sanedge.paymentgateway.transaction.repository.TransactionStatusRepository;
import com.sanedge.paymentgateway.transaction.service.TransactionStatusService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionStatusImplService implements TransactionStatusService {
    private final TransactionStatusRepository transactionStatusRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TransactionResponseMonthStatusSuccess>> findMonthStatusSuccess(
            MonthStatusTransaction req) {
        Long year = (long) req.getYear();
        int month = req.getMonth();

        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .put("month", String.valueOf(month))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthStatusSuccess", "find_month_status_success", attrs, () -> {
            log.info("📊 Fetching monthly SUCCESS transaction status for year: {}, month: {}", year, month);

            if (!isValidYearMonth(year, month)) {
                log.error("❌ Invalid year/month: year={}, month={}", year, month);
                return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                        .status("error")
                        .message("Invalid year/month")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                LocalDate nextMonth = currentMonth.plusMonths(1);

                log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                        nextMonth.getMonthValue());

                List<TransactionMonthStatusSuccess> results = transactionStatusRepository
                        .findMonthTransactionStatusSuccess(year, month, (long) nextMonth.getYear(),
                                nextMonth.getMonthValue());

                List<TransactionResponseMonthStatusSuccess> response = results.stream()
                        .map(TransactionResponseMonthStatusSuccess::from)
                        .toList();

                log.info("✅ Found {} monthly SUCCESS transaction status records for year={}, month={}",
                        response.size(), year, month);

                return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + response.size() + " monthly SUCCESS transaction status")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly SUCCESS transaction status for year={}, month={}", year,
                        month, e);
                return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                        .status("error")
                        .message("Failed to fetch monthly SUCCESS transaction status")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyStatusSuccess", "find_yearly_status_success", attrs, () -> {
            log.info("📊 Fetching yearly SUCCESS transaction status for year: {}", year);

            if (!isValidYear(year)) {
                log.error("❌ Invalid year: {}", year);
                return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Invalid year")
                        .build();
            }

            try {
                List<TransactionYearStatusSuccess> results = transactionStatusRepository
                        .findYearlyTransactionStatusSuccess(year);

                List<TransactionResponseYearStatusSuccess> response = results.stream()
                        .map(TransactionResponseYearStatusSuccess::from)
                        .toList();

                log.info("✅ Found {} yearly SUCCESS transaction status records for year {}", response.size(),
                        year);

                return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + response.size() + " yearly SUCCESS transaction status")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly SUCCESS transaction status for year={}", year, e);
                return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                        .status("error")
                        .message("Failed to fetch yearly SUCCESS transaction status")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionResponseMonthStatusFailed>> findMonthStatusFailed(
            MonthStatusTransaction req) {
        Long year = (long) req.getYear();
        int month = req.getMonth();

        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .put("month", String.valueOf(month))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthStatusFailed", "find_month_status_failed", attrs, () -> {
            log.info("📊 Fetching monthly FAILED transaction status for year: {}, month: {}", year, month);

            if (!isValidYearMonth(year, month)) {
                log.error("❌ Invalid year/month: year={}, month={}", year, month);
                return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Invalid year/month")
                        .build();
            }

            try {
                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                LocalDate nextMonth = currentMonth.plusMonths(1);

                List<TransactionMonthStatusFailed> results = transactionStatusRepository
                        .findMonthTransactionStatusFailed(year, month, (long) nextMonth.getYear(),
                                nextMonth.getMonthValue());

                List<TransactionResponseMonthStatusFailed> response = results.stream()
                        .map(TransactionResponseMonthStatusFailed::from)
                        .toList();

                log.info("✅ Found {} monthly FAILED transaction status records for year={}, month={}",
                        response.size(), year, month);

                return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + response.size() + " monthly FAILED transaction status")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly FAILED transaction status for year={}, month={}", year,
                        month, e);
                return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly FAILED transaction status")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionResponseYearStatusFailed>> findYearlyStatusFailed(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyStatusFailed", "find_yearly_status_failed", attrs, () -> {
            log.info("📊 Fetching yearly FAILED transaction status for year: {}", year);

            if (!isValidYear(year)) {
                log.error("❌ Invalid year: {}", year);
                return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                        .status("error")
                        .message("Invalid year")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionYearStatusFailed> results = transactionStatusRepository
                        .findYearlyTransactionStatusFailed(year);

                List<TransactionResponseYearStatusFailed> response = results.stream()
                        .map(TransactionResponseYearStatusFailed::from)
                        .toList();

                log.info("✅ Found {} yearly FAILED transaction status records for year {}", response.size(),
                        year);

                return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                        .status("success")
                        .data(response)
                        .message("Found " + response.size() + " yearly FAILED transaction status")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly FAILED transaction status for year={}", year, e);
                return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly FAILED transaction status")
                        .build();
            }
        });
    }

    private boolean isValidYearMonth(Long year, Integer month) {
        return year != null && month != null && year >= 1 && year <= 9999 && month >= 1 && month <= 12;
    }

    private boolean isValidYear(Long year) {
        return year != null && year >= 1 && year <= 9999;
    }
}
