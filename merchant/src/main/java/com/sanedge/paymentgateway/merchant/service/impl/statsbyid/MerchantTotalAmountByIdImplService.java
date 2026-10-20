package com.sanedge.paymentgateway.merchant.service.impl.statsbyid;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearTotalAmountMerchant;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.repository.statsbyid.MerchantTotalAmountByIdRepository;
import com.sanedge.paymentgateway.merchant.service.stats.totalamount.MerchantTotalAmountByIdService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantTotalAmountByIdImplService implements MerchantTotalAmountByIdService {
        private final MerchantTotalAmountByIdRepository merchantTotalAmountByIdRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmountById(
                        MonthYearTotalAmountMerchant req) {
                String merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_total_amount_by_id")
                                .put("service.name", "merchant-total-amount-by-id-service")
                                .put("merchantId", String.valueOf(merchantId))
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthTotalAmountById", "find_month_total_amount_by_id", attrs,
                                () -> {
                                        log.info("📊 Fetching monthly total merchant amounts for merchantId={} and year={}", merchantId, year);

                                        try {
                                                validateYear(year);

                                                LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);

                                                List<MerchantMonthlyTotalAmount> monthlyTotals = merchantTotalAmountByIdRepository
                                                                .findMonthlyTotalAmountById(merchantId, yearDate);

                                                List<MerchantResponseMonthlyTotalAmount> response = monthlyTotals.stream()
                                                                .map(MerchantResponseMonthlyTotalAmount::from)
                                                                .toList();

                                                log.info("✅ Found {} monthly total amount records for merchantId={} in year={}",
                                                                response.size(), merchantId, year);

                                                return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                                                                .status("success")
                                                                .data(response)
                                                                .message("Successfully fetched monthly total merchant amounts for year=" + year)
                                                                .build();
                                        } catch (Exception e) {
                                                log.error("❌ Failed to fetch monthly total merchant amounts for merchantId={} year={}",
                                                                merchantId, year, e);
                                                return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                                                                .status("error")
                                                                .data(Collections.emptyList())
                                                                .message("Failed to fetch monthly total merchant amounts for year=" + year)
                                                                .build();
                                        }
                                });
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmountById(
                        MonthYearTotalAmountMerchant req) {
                String merchantId = req.getMerchantId();
                Long year = (long) req.getYear();

                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_total_amount_by_id")
                                .put("service.name", "merchant-total-amount-by-id-service")
                                .put("merchantId", String.valueOf(merchantId))
                                .put("year", String.valueOf(year))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearTotalAmountById", "find_year_total_amount_by_id", attrs,
                                () -> {
                                        log.info("📊 Fetching yearly total merchant amounts for merchantId={} and year={}", merchantId, year);

                                        try {
                                                validateYear(year);

                                                Long prevYear = year - 1L;

                                                List<MerchantYearlyTotalAmount> yearlyTotals = merchantTotalAmountByIdRepository
                                                                .findYearlyTotalAmountById(merchantId, year, prevYear);

                                                List<MerchantResponseYearlyTotalAmount> response = yearlyTotals.stream()
                                                                .map(MerchantResponseYearlyTotalAmount::from)
                                                                .toList();

                                                log.info("✅ Found {} yearly total amount records for merchantId={} in year={}", response.size(),
                                                                merchantId, year);

                                                return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                                                                .status("success")
                                                                .data(response)
                                                                .message("Successfully fetched yearly total merchant amounts for year=" + year)
                                                                .build();
                                        } catch (Exception e) {
                                                log.error("❌ Failed to fetch yearly total merchant amounts for merchantId={} year={}",
                                                                merchantId, year, e);
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
