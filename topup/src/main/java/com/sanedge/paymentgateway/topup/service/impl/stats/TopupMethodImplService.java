package com.sanedge.paymentgateway.topup.service.impl.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupMonthMethodResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupYearlyMethodResponse;
import com.sanedge.paymentgateway.topup.models.TopupMonthMethod;
import com.sanedge.paymentgateway.topup.models.TopupYearMethod;
import com.sanedge.paymentgateway.topup.repository.stats.TopupMethodRepository;
import com.sanedge.paymentgateway.topup.service.stats.method.TopupMethodService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupMethodImplService implements TopupMethodService {
    private final TopupMethodRepository topupMethodRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TopupMonthMethodResponse>> findMonthlyMethods(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyMethods", "find_monthly_methods", attrs, () -> {
            log.info("📊 Fetching monthly topup methods stats for year={}", year);

            validateYear(year);

            try {
                List<TopupMonthMethod> monthlyMethods = topupMethodRepository.findMonthlyTopupMethods(year);

                log.info("✅ Retrieved {} monthly topup method records for year={}", monthlyMethods.size(), year);

                List<TopupMonthMethodResponse> response = monthlyMethods.stream()
                        .map(TopupMonthMethodResponse::from)
                        .toList();

                return ApiResponse.<List<TopupMonthMethodResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly topup methods for year=" + year)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly topup methods for year={}", year, e);
                return ApiResponse.<List<TopupMonthMethodResponse>>builder()
                        .status("error")
                        .message("Failed to fetch monthly topup methods for year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TopupYearlyMethodResponse>> findYearlyMethods(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyMethods", "find_yearly_methods", attrs, () -> {
            log.info("📊 Fetching yearly topup methods stats until year={}", year);

            validateYear(year);

            try {
                List<TopupYearMethod> yearlyMethods = topupMethodRepository.findYearlyTopupMethods(year);

                log.info("✅ Retrieved {} yearly topup method records until year={}", yearlyMethods.size(), year);

                List<TopupYearlyMethodResponse> response = yearlyMethods.stream()
                        .map(TopupYearlyMethodResponse::from)
                        .toList();

                return ApiResponse.<List<TopupYearlyMethodResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly topup methods until year=" + year)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly topup methods until year={}", year, e);
                return ApiResponse.<List<TopupYearlyMethodResponse>>builder()
                        .status("error")
                        .message("Failed to fetch yearly topup methods until year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
