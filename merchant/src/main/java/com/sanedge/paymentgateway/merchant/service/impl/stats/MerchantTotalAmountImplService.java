package com.sanedge.paymentgateway.merchant.service.impl.stats;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.repository.stats.MerchantTotalAmountRepository;
import com.sanedge.paymentgateway.merchant.service.stats.totalamount.MerchantTotalAmountService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantTotalAmountImplService implements MerchantTotalAmountService {
    private final MerchantTotalAmountRepository merchantTotalAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmount(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_month_total_amount")
                .put("service.name", "merchant-total-amount-service")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthTotalAmount", "find_month_total_amount", attrs, () -> {
            log.info("📊 Fetching monthly total merchant amounts for year: {}", year);

            try {
                validateYear(year);
                LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);

                List<MerchantMonthlyTotalAmount> monthlyTotals = merchantTotalAmountRepository
                        .findMonthlyTotalAmount(yearDate);

                List<MerchantResponseMonthlyTotalAmount> response = monthlyTotals.stream()
                        .map(MerchantResponseMonthlyTotalAmount::from)
                        .toList();

                log.info("✅ Found {} monthly total amount records for year {}", response.size(), year);

                return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly total merchant amounts for year=" + year)
                        .build();
            } catch (Exception e) {
                log.error("❌ Failed to fetch monthly total merchant amounts for year={}", year, e);
                return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly total merchant amounts for year=" + year)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmount(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_year_total_amount")
                .put("service.name", "merchant-total-amount-service")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearTotalAmount", "find_year_total_amount", attrs, () -> {
            log.info("📊 Fetching yearly total merchant amounts for year: {}", year);

            try {
                validateYear(year);
                Long prevYear = year - 1L;

                List<MerchantYearlyTotalAmount> yearlyTotals = merchantTotalAmountRepository.findYearlyTotalAmount(year,
                        prevYear);

                List<MerchantResponseYearlyTotalAmount> response = yearlyTotals.stream()
                        .map(MerchantResponseYearlyTotalAmount::from)
                        .toList();

                log.info("✅ Found {} yearly total amount records for year {}", response.size(), year);

                return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly total merchant amounts for year=" + year)
                        .build();
            } catch (Exception e) {
                log.error("❌ Failed to fetch yearly total merchant amounts for year={}", year, e);
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
