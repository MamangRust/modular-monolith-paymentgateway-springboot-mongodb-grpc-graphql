package com.sanedge.paymentgateway.topup.service.impl.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupMonthAmountResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupYearlyAmountResponse;
import com.sanedge.paymentgateway.topup.models.TopupMonthAmount;
import com.sanedge.paymentgateway.topup.models.TopupYearAmount;
import com.sanedge.paymentgateway.topup.repository.stats.TopupAmountRepository;
import com.sanedge.paymentgateway.topup.service.stats.amount.TopupAmountService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupAmountImplService implements TopupAmountService {
    private final TopupAmountRepository topupAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyAmounts", "find_monthly_amounts", attrs, () -> {
            log.info("📊 Fetching monthly topup amounts for year={}", year);

            validateYear(year);

            try {
                List<TopupMonthAmount> amounts = topupAmountRepository.findMonthlyTopupAmounts(year);

                log.info("✅ Retrieved {} monthly topup amount records for year={}", amounts.size(), year);

                List<TopupMonthAmountResponse> response = amounts.stream()
                        .map(TopupMonthAmountResponse::from)
                        .toList();

                return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly topup amounts for year=" + year)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly topup amounts for year={}", year, e);
                return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                        .status("error")
                        .message("Failed to fetch monthly topup amounts for year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyAmounts", "find_yearly_amounts", attrs, () -> {
            log.info("📊 Fetching yearly topup amounts until year={}", year);

            validateYear(year);

            try {
                List<TopupYearAmount> amounts = topupAmountRepository.findYearlyTopupAmounts(year);

                log.info("✅ Retrieved {} yearly topup amount records until year={}", amounts.size(), year);

                List<TopupYearlyAmountResponse> response = amounts.stream()
                        .map(TopupYearlyAmountResponse::from)
                        .toList();

                return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly topup amounts until year=" + year)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly topup amounts until year={}", year, e);
                return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                        .status("error")
                        .message("Failed to fetch yearly topup amounts until year=" + year)
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
