package com.sanedge.paymentgateway.topup.service.impl.stats;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.topup.domain.requests.stats.MonthTopupStatus;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusSuccess;
import com.sanedge.paymentgateway.topup.models.TopupMonthStatusFailed;
import com.sanedge.paymentgateway.topup.models.TopupMonthStatusSuccess;
import com.sanedge.paymentgateway.topup.models.TopupYearStatusFailed;
import com.sanedge.paymentgateway.topup.models.TopupYearStatusSuccess;
import com.sanedge.paymentgateway.topup.repository.stats.TopupStatusRepository;
import com.sanedge.paymentgateway.topup.service.stats.status.TopupStatusService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupStatusImplService implements TopupStatusService {
    private final TopupStatusRepository topupStatusRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthTopupStatus req) {
        Long year = (long) req.getYear();
        int month = req.getMonth();
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .put("month", String.valueOf(month))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthStatusSuccess", "find_month_status_success", attrs, () -> {
            log.info("📊 Fetching monthly SUCCESS topup status for year={} month={}", year, month);

            validateYearMonth(year, month);

            LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
            LocalDate nextMonth = currentMonth.plusMonths(1);

            log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                    nextMonth.getMonthValue());

            try {
                List<TopupMonthStatusSuccess> data = topupStatusRepository.findMonthTopupStatusSuccess(
                        year, month, (long) nextMonth.getYear(), nextMonth.getMonthValue());

                List<TopupResponseMonthStatusSuccess> response = data.stream()
                        .map(TopupResponseMonthStatusSuccess::from)
                        .toList();

                log.info("✅ Retrieved {} records for SUCCESS status month={} year={}", response.size(), month, year);

                return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly SUCCESS topup status")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly SUCCESS topup status month={} year={}", month, year, e);
                return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly SUCCESS topup status")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyStatusSuccess", "find_yearly_status_success", attrs, () -> {
            log.info("📊 Fetching yearly SUCCESS topup status for year={}", year);
            validateYear(year);

            try {
                List<TopupYearStatusSuccess> data = topupStatusRepository.findYearlyTopupStatusSuccess(year);
                List<TopupResponseYearStatusSuccess> response = data.stream()
                        .map(TopupResponseYearStatusSuccess::from)
                        .toList();

                log.info("✅ Retrieved {} records for yearly SUCCESS status year={}", response.size(), year);

                return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly SUCCESS topup status")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly SUCCESS topup status year={}", year, e);
                return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly SUCCESS topup status")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatus req) {
        Long year = (long) req.getYear();
        int month = req.getMonth();
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .put("month", String.valueOf(month))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthStatusFailed", "find_month_status_failed", attrs, () -> {
            log.info("📊 Fetching monthly FAILED topup status for year={} month={}", year, month);

            validateYearMonth(year, month);

            LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
            LocalDate nextMonth = currentMonth.plusMonths(1);

            log.debug("🗓️ Querying data for: {}/{} and {}/{}", year, month, (long) nextMonth.getYear(),
                    nextMonth.getMonthValue());

            try {
                List<TopupMonthStatusFailed> data = topupStatusRepository.findMonthTopupStatusFailed(
                        year, month, (long) nextMonth.getYear(), nextMonth.getMonthValue());

                List<TopupResponseMonthStatusFailed> response = data.stream()
                        .map(TopupResponseMonthStatusFailed::from)
                        .toList();

                log.info("✅ Retrieved {} records for FAILED status month={} year={}", response.size(), month, year);

                return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly FAILED topup status")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly FAILED topup status month={} year={}", month, year, e);
                return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly FAILED topup status")
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyStatusFailed", "find_yearly_status_failed", attrs, () -> {
            log.info("📊 Fetching yearly FAILED topup status for year={}", year);
            validateYear(year);

            try {
                List<TopupYearStatusFailed> data = topupStatusRepository.findYearlyTopupStatusFailed(year);
                List<TopupResponseYearStatusFailed> response = data.stream()
                        .map(TopupResponseYearStatusFailed::from)
                        .toList();

                log.info("✅ Retrieved {} records for yearly FAILED status year={}", response.size(), year);

                return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly FAILED topup status")
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly FAILED topup status year={}", year, e);
                return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly FAILED topup status")
                        .build();
            }
        });
    }

    private void validateYearMonth(Long year, Integer month) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
        if (month == null || month < 1 || month > 12) {
            log.error("❌ Invalid month: {}", month);
            throw new IllegalArgumentException("Invalid month");
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
