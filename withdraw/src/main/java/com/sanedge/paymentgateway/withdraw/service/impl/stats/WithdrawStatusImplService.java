package com.sanedge.paymentgateway.withdraw.service.impl.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.withdraw.domain.requests.MonthStatusWithdraw;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusSuccess;
import com.sanedge.paymentgateway.withdraw.repository.stats.WithdrawStatusRepository;
import com.sanedge.paymentgateway.withdraw.service.stats.status.WithdrawStatusService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawStatusImplService implements WithdrawStatusService {
        private final WithdrawStatusRepository withdrawStatusRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<WithdrawResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusWithdraw req) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthStatusSuccess", "find_month_success", attrs, () -> {
                        try {
                                Long year = (long) req.getYear();
                                int month = req.getMonth();

                                log.info("📊 Fetching monthly SUCCESS withdraw status for year: {}, month: {}", year, month);
                                validateYearMonth(year, month);

                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                List<WithdrawMonthStatusSuccess> results = withdrawStatusRepository
                                                .findMonthWithdrawStatusSuccess(
                                                                year, month, (long) nextMonth.getYear(), nextMonth.getMonthValue());

                                List<WithdrawResponseMonthStatusSuccess> response = results.stream()
                                                .map(WithdrawResponseMonthStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                                                .status("success")
                                                .message("Monthly SUCCESS withdraw status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly SUCCESS withdraw status", e);
                                return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                                                .status("error")
                                                .message("Failed to fetch monthly SUCCESS withdraw status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<WithdrawResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyStatusSuccess", "find_year_success", attrs, () -> {
                        try {
                                log.info("📊 Fetching yearly SUCCESS withdraw status for year: {}", year);
                                validateYear(year);

                                List<WithdrawYearStatusSuccess> results = withdrawStatusRepository
                                                .findYearlyWithdrawStatusSuccess(year);

                                List<WithdrawResponseYearStatusSuccess> response = results.stream()
                                                .map(WithdrawResponseYearStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                                                .status("success")
                                                .message("Yearly SUCCESS withdraw status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly SUCCESS withdraw status", e);
                                return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                                                .status("error")
                                                .message("Failed to fetch yearly SUCCESS withdraw status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<WithdrawResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusWithdraw req) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthStatusFailed", "find_month_failed", attrs, () -> {
                        try {
                                Long year = (long) req.getYear();
                                int month = req.getMonth();

                                log.info("📊 Fetching monthly FAILED withdraw status for year: {}, month: {}", year, month);
                                validateYearMonth(year, month);

                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                List<WithdrawMonthStatusFailed> results = withdrawStatusRepository
                                                .findMonthWithdrawStatusFailed(
                                                                year, month, (long) nextMonth.getYear(), nextMonth.getMonthValue());

                                List<WithdrawResponseMonthStatusFailed> response = results.stream()
                                                .map(WithdrawResponseMonthStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                                                .status("success")
                                                .message("Monthly FAILED withdraw status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly FAILED withdraw status", e);
                                return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                                                .status("error")
                                                .message("Failed to fetch monthly FAILED withdraw status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<WithdrawResponseYearStatusFailed>> findYearlyStatusFailed(Long year) {
                Attributes attrs = Attributes.builder()
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyStatusFailed", "find_year_failed", attrs, () -> {
                        try {
                                log.info("📊 Fetching yearly FAILED withdraw status for year: {}", year);
                                validateYear(year);

                                List<WithdrawYearStatusFailed> results = withdrawStatusRepository
                                                .findYearlyWithdrawStatusFailed(year);

                                List<WithdrawResponseYearStatusFailed> response = results.stream()
                                                .map(WithdrawResponseYearStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                                                .status("success")
                                                .message("Yearly FAILED withdraw status fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly FAILED withdraw status", e);
                                return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                                                .status("error")
                                                .message("Failed to fetch yearly FAILED withdraw status")
                                                .data(null)
                                                .build();
                        }
                });
        }

        private void validateYearMonth(Long year, Integer month) {
                if (year < 1 || year > 9999)
                        throw new IllegalArgumentException("Invalid year");
                if (month < 1 || month > 12)
                        throw new IllegalArgumentException("Invalid month");
        }

        private void validateYear(Long year) {
                if (year < 1 || year > 9999)
                        throw new IllegalArgumentException("Invalid year");
        }
}
