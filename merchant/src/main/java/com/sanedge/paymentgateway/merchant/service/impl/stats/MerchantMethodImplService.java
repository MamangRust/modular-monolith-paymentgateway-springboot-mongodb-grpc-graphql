package com.sanedge.paymentgateway.merchant.service.impl.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.repository.stats.MerchantMethodRepository;
import com.sanedge.paymentgateway.merchant.service.stats.method.MerchantMethodService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantMethodImplService implements MerchantMethodService {
    private final MerchantMethodRepository merchantMethodRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethod(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_month_method")
                .put("service.name", "merchant-method-service")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthMethod", "find_month_method", attrs, () -> {
            log.info("📊 Fetching monthly payment method stats for year: {}", year);

            try {
                validateYear(year);

                List<MerchantMonthlyPaymentMethod> monthlyMethods = merchantMethodRepository
                        .findMonthlyPaymentMethods(year);

                List<MerchantResponseMonthlyPaymentMethod> response = monthlyMethods.stream()
                        .map(MerchantResponseMonthlyPaymentMethod::from)
                        .toList();

                log.info("✅ Found {} monthly payment method records for year {}", response.size(), year);

                return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly payment method stats for year=" + year)
                        .build();
            } catch (Exception e) {
                log.error("❌ Failed to fetch monthly payment method stats for year={}", year, e);
                return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly payment method stats for year=" + year)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethod(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_year_method")
                .put("service.name", "merchant-method-service")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearMethod", "find_year_method", attrs, () -> {
            log.info("📊 Fetching yearly payment method stats for year: {}", year);

            try {
                validateYear(year);

                List<MerchantYearlyPaymentMethod> yearlyMethods = merchantMethodRepository.findYearlyPaymentMethods(year);

                List<MerchantResponseYearlyPaymentMethod> response = yearlyMethods.stream()
                        .map(MerchantResponseYearlyPaymentMethod::from)
                        .toList();

                log.info("✅ Found {} yearly payment method records for year {}", response.size(), year);

                return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly payment method stats for year=" + year)
                        .build();
            } catch (Exception e) {
                log.error("❌ Failed to fetch yearly payment method stats for year={}", year, e);
                return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly payment method stats for year=" + year)
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
