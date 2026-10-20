package com.sanedge.paymentgateway.transfer.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthStatusTransfer;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess;
import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.models.TransferYearStatusFailed;
import com.sanedge.paymentgateway.transfer.models.TransferYearStatusSuccess;
import com.sanedge.paymentgateway.transfer.repository.stats.TransferStatusRepository;
import com.sanedge.paymentgateway.transfer.service.TransferStatusService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferStatusImplService implements TransferStatusService {

        private final TransferStatusRepository transferStatusRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<TransferResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusTransfer req) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthTransferStatusSuccess", "find_month_status_success", attrs, () -> {
                        Long year = (long) req.getYear();
                        int month = req.getMonth();

                        log.info("📊 Fetching monthly SUCCESS transfer status for year: {}, month: {}", year, month);

                        validateYearMonth(year, month);

                        try {
                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                                                nextMonth.getMonthValue());

                                List<TransferMonthStatusSuccess> results = transferStatusRepository
                                                .findMonthTransferStatusSuccess(year, month, (long) nextMonth.getYear(),
                                                                nextMonth.getMonthValue());

                                log.info("✅ Found {} monthly SUCCESS transfer status records for year={}, month={}",
                                                results.size(), year, month);

                                List<TransferResponseMonthStatusSuccess> response = results.stream()
                                                .map(TransferResponseMonthStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseMonthStatusSuccess>>builder()
                                                .status("success")
                                                .message("Monthly SUCCESS transfer status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly SUCCESS transfer status for year={}, month={}", year,
                                                month);

                                return ApiResponse.<List<TransferResponseMonthStatusSuccess>>builder()
                                                .status("error")
                                                .message("Failed to retrieve monthly SUCCESS transfer status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<TransferResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransfer req) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthTransferStatusFailed", "find_month_status_failed", attrs, () -> {
                        Long year = (long) req.getYear();
                        int month = req.getMonth();

                        log.info("📊 Fetching monthly FAILED transfer status for year: {}, month: {}", year, month);

                        validateYearMonth(year, month);

                        try {
                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                                                nextMonth.getMonthValue());

                                List<TransferMonthStatusFailed> results = transferStatusRepository
                                                .findMonthTransferStatusFailed(year, month, (long) nextMonth.getYear(),
                                                                nextMonth.getMonthValue());

                                log.info("✅ Found {} monthly FAILED transfer status records for year={}, month={}",
                                                results.size(), year, month);

                                List<TransferResponseMonthStatusFailed> response = results.stream()
                                                .map(TransferResponseMonthStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseMonthStatusFailed>>builder()
                                                .status("success")
                                                .message("Monthly FAILED transfer status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly FAILED transfer status for year={}, month={}", year,
                                                month);

                                return ApiResponse.<List<TransferResponseMonthStatusFailed>>builder()
                                                .status("error")
                                                .message("Failed to retrieve monthly FAILED transfer status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<TransferResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyTransferStatusSuccess", "find_yearly_status_success", attrs, () -> {
                        log.info("📊 Fetching yearly SUCCESS transfer status for year: {}", year);
                        validateYear(year);

                        try {
                                List<TransferYearStatusSuccess> results = transferStatusRepository
                                                .findYearlyTransferStatusSuccess(year);

                                log.info("✅ Found {} yearly SUCCESS transfer status records for year {}", results.size(), year);

                                List<TransferResponseYearStatusSuccess> response = results.stream()
                                                .map(TransferResponseYearStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseYearStatusSuccess>>builder()
                                                .status("success")
                                                .message("Yearly SUCCESS transfer status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly SUCCESS transfer status for year={}", year);

                                return ApiResponse.<List<TransferResponseYearStatusSuccess>>builder()
                                                .status("error")
                                                .message("Failed to retrieve yearly SUCCESS transfer status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<TransferResponseYearStatusFailed>> findYearlyStatusFailed(Long year) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyTransferStatusFailed", "find_yearly_status_failed", attrs, () -> {
                        log.info("📊 Fetching yearly FAILED transfer status for year: {}", year);
                        validateYear(year);

                        try {
                                List<TransferYearStatusFailed> results = transferStatusRepository
                                                .findYearlyTransferStatusFailed(year);

                                log.info("✅ Found {} yearly FAILED transfer status records for year {}", results.size(), year);

                                List<TransferResponseYearStatusFailed> response = results.stream()
                                                .map(TransferResponseYearStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<TransferResponseYearStatusFailed>>builder()
                                                .status("success")
                                                .message("Yearly FAILED transfer status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly FAILED transfer status for year={}", year);

                                return ApiResponse.<List<TransferResponseYearStatusFailed>>builder()
                                                .status("error")
                                                .message("Failed to retrieve yearly FAILED transfer status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        private void validateYearMonth(Long year, Integer month) {
                if (year < 1 || year > 9999 || month < 1 || month > 12) {
                        log.error("❌ Invalid year/month: year={}, month={}", year, month);
                        throw new IllegalArgumentException("Invalid year or month");
                }
        }

        private void validateYear(Long year) {
                if (year < 1 || year > 9999) {
                        log.error("❌ Invalid year: {}", year);
                        throw new IllegalArgumentException("Invalid year");
                }
        }
}
