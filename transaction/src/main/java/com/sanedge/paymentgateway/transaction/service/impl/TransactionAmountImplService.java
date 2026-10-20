package com.sanedge.paymentgateway.transaction.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthAmountResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearlyAmountResponse;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthAmount;
import com.sanedge.paymentgateway.transaction.models.TransactionYearAmount;
import com.sanedge.paymentgateway.transaction.repository.TransactionAmountRepository;
import com.sanedge.paymentgateway.transaction.service.TransactionAmountService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionAmountImplService implements TransactionAmountService {
    private final TransactionAmountRepository transactionAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyAmounts", "find_monthly_amounts", attrs, () -> {
            log.info("📊 Fetching monthly transaction amounts for year={}", year);

            if (year == null || year < 1 || year > 9999) {
                log.error("❌ Invalid year: {}", year);
                return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                        .status("error")
                        .message("Invalid year provided")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionMonthAmount> amounts = transactionAmountRepository.findMonthlyAmounts(year);

                List<TransactionMonthAmountResponse> response = amounts.stream()
                        .map(TransactionMonthAmountResponse::from)
                        .toList();

                log.info("✅ Retrieved {} monthly transaction amount records for year={}", response.size(), year);

                return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly transaction amounts for year=" + year)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly transaction amounts for year={}", year, e);
                return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                        .status("error")
                        .message("Failed to fetch monthly transaction amounts for year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyAmounts", "find_yearly_amounts", attrs, () -> {
            log.info("📊 Fetching yearly transaction amounts until year={}", year);

            if (year == null || year < 1 || year > 9999) {
                log.error("❌ Invalid year: {}", year);
                return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                        .status("error")
                        .message("Invalid year provided")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionYearAmount> amounts = transactionAmountRepository.findYearlyAmounts(year);

                List<TransactionYearlyAmountResponse> response = amounts.stream()
                        .map(TransactionYearlyAmountResponse::from)
                        .toList();

                log.info("✅ Retrieved {} yearly transaction amount records until year={}", response.size(), year);

                return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                        .status("success")
                        .message("Successfully fetched yearly transaction amounts until year=" + year)
                        .data(response)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly transaction amounts until year={}", year, e);
                return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                        .status("error")
                        .message("Failed to fetch yearly transaction amounts until year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }
}
