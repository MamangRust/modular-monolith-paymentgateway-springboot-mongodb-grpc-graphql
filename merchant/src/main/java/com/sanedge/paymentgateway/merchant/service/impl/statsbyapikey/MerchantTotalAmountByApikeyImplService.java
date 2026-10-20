package com.sanedge.paymentgateway.merchant.service.impl.statsbyapikey;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearTotalAmountApiKey;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.repository.statsbyapikey.MerchantTotalAmountByApiKeyRepository;
import com.sanedge.paymentgateway.merchant.service.stats.totalamount.MerchantTotalAmountByApiKeyService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantTotalAmountByApikeyImplService implements MerchantTotalAmountByApiKeyService {
        private final MerchantTotalAmountByApiKeyRepository merchantTotalAmountByApiKeyRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmountByApiKey(
                        MonthYearTotalAmountApiKey req) {
                String apiKey = req.getApiKey();
                Long year = (long) req.getYear();

                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_total_amount_by_apikey")
                                .put("service.name", "merchant-total-amount-by-apikey-service")
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthTotalAmountByApiKey", "find_month_total_amount_by_apikey",
                                attrs, () -> {
                                        log.info("📊 Fetching monthly total merchant amounts for year: {} with apiKey: {}", year, apiKey);

                                        try {
                                                validateYear(year);

                                                LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);

                                                List<MerchantMonthlyTotalAmount> monthlyTotals = merchantTotalAmountByApiKeyRepository
                                                                .findMonthlyTotalAmountByApiKey(apiKey, yearDate);

                                                List<MerchantResponseMonthlyTotalAmount> response = monthlyTotals.stream()
                                                                .map(MerchantResponseMonthlyTotalAmount::from)
                                                                .toList();

                                                log.info("✅ Found {} monthly total amount records for year {} with apiKey {}", response.size(),
                                                                year, apiKey);

                                                return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                                                                .status("success")
                                                                .data(response)
                                                                .message("Successfully fetched monthly total merchant amounts for year=" + year)
                                                                .build();
                                        } catch (Exception e) {
                                                log.error("❌ Failed to fetch monthly total merchant amounts for year={} apiKey={}", year,
                                                                apiKey, e);
                                                return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                                                                .status("error")
                                                                .data(Collections.emptyList())
                                                                .message("Failed to fetch monthly total merchant amounts for year=" + year)
                                                                .build();
                                        }
                                });
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmountByApiKey(
                        MonthYearTotalAmountApiKey req) {
                String apiKey = req.getApiKey();
                Long year = (long) req.getYear();

                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_total_amount_by_apikey")
                                .put("service.name", "merchant-total-amount-by-apikey-service")
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearTotalAmountByApiKey", "find_year_total_amount_by_apikey",
                                attrs, () -> {
                                        log.info("📊 Fetching yearly total merchant amounts for year: {} with apiKey: {}", year, apiKey);

                                        try {
                                                validateYear(year);

                                                Long prevYear = year - 1L;

                                                List<MerchantYearlyTotalAmount> yearlyTotals = merchantTotalAmountByApiKeyRepository
                                                                .findYearlyTotalAmountByApiKey(apiKey, year, prevYear);

                                                List<MerchantResponseYearlyTotalAmount> response = yearlyTotals.stream()
                                                                .map(MerchantResponseYearlyTotalAmount::from)
                                                                .toList();

                                                log.info("✅ Found {} yearly total amount records for year {} with apiKey {}", response.size(),
                                                                year, apiKey);

                                                return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                                                                .status("success")
                                                                .data(response)
                                                                .message("Successfully fetched yearly total merchant amounts for year=" + year)
                                                                .build();
                                        } catch (Exception e) {
                                                log.error("❌ Failed to fetch yearly total merchant amounts for year={} apiKey={}", year, apiKey,
                                                                e);
                                                return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                                                                .status("error")
                                                                .data(Collections.emptyList())
                                                                .message("Failed to fetch yearly total merchant amounts for year=" + year)
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
