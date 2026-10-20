package com.sanedge.paymentgateway.merchant.service.impl.statsbyid;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearAmountMerchant;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyAmount;
import com.sanedge.paymentgateway.merchant.repository.statsbyid.MerchantAmountByIdRepository;
import com.sanedge.paymentgateway.merchant.service.stats.amount.MerchantAmountByIdService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAmountByIdImplService implements MerchantAmountByIdService {
        private final MerchantAmountByIdRepository merchantAmountByIdRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmountById(MonthYearAmountMerchant req) {
                String merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_amount_by_id")
                                .put("service.name", "merchant-amount-by-id-service")
                                .put("merchantId", String.valueOf(merchantId))
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthAmountById", "find_month_amount_by_id", attrs, () -> {
                        log.info("📊 Fetching monthly merchant amounts for merchantId={} and year={}", merchantId, year);

                        try {
                                validateYear(year);

                                List<MerchantMonthlyAmount> monthlyAmounts = merchantAmountByIdRepository
                                                .findMonthlyAmountById(merchantId, year);

                                List<MerchantResponseMonthlyAmount> response = monthlyAmounts.stream()
                                                .map(MerchantResponseMonthlyAmount::from)
                                                .toList();

                                log.info("✅ Found {} monthly amount records for merchantId={} in year={}", response.size(),
                                                merchantId, year);

                                return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                                                .status("success")
                                                .data(response)
                                                .message("Successfully fetched monthly merchant amounts for year=" + year)
                                                .build();
                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly merchant amounts for merchantId={} year={}", merchantId,
                                                year, e);
                                return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                                                .status("error")
                                                .data(Collections.emptyList())
                                                .message("Failed to fetch monthly merchant amounts for year=" + year)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmountById(MonthYearAmountMerchant req) {
                String merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_amount_by_id")
                                .put("service.name", "merchant-amount-by-id-service")
                                .put("merchantId", String.valueOf(merchantId))
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearAmountById", "find_year_amount_by_id", attrs, () -> {
                        log.info("📊 Fetching yearly merchant amounts for merchantId={} and year={}", merchantId, year);

                        try {
                                validateYear(year);

                                List<MerchantYearlyAmount> yearlyAmounts = merchantAmountByIdRepository
                                                .findYearlyAmountById(merchantId, year);

                                List<MerchantResponseYearlyAmount> response = yearlyAmounts.stream()
                                                .map(MerchantResponseYearlyAmount::from)
                                                .toList();

                                log.info("✅ Found {} yearly amount records for merchantId={} in year={}", response.size(),
                                                merchantId, year);

                                return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                                                .status("success")
                                                .data(response)
                                                .message("Successfully fetched yearly merchant amounts for year=" + year)
                                                .build();
                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly merchant amounts for merchantId={} year={}", merchantId,
                                                year, e);
                                return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                                                .status("error")
                                                .data(Collections.emptyList())
                                                .message("Failed to fetch yearly merchant amounts for year=" + year)
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
