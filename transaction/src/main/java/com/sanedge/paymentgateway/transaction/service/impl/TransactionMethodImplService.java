package com.sanedge.paymentgateway.transaction.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthMethod;
import com.sanedge.paymentgateway.transaction.models.TransactionYearMethod;
import com.sanedge.paymentgateway.transaction.repository.TransactionMethodRepository;
import com.sanedge.paymentgateway.transaction.service.TransactionMethodService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionMethodImplService implements TransactionMethodService {
    private final TransactionMethodRepository transactionMethodRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyMethod", "find_monthly_method", attrs, () -> {
            log.info("📊 Fetching monthly transaction methods for year={}", year);

            if (year == null || year < 1 || year > 9999) {
                log.error("❌ Invalid year provided: {}", year);
                return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Invalid year provided")
                        .build();
            }

            try {
                List<TransactionMonthMethod> methods = transactionMethodRepository.findMonthlyPaymentMethods(year);

                List<TransactionMonthMethodResponse> response = methods.stream()
                        .map(TransactionMonthMethodResponse::from)
                        .toList();

                log.info("✅ Retrieved {} monthly transaction method records for year={}", response.size(), year);

                return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                        .status("success")
                        .message("Successfully fetched monthly transaction methods for year=" + year)
                        .data(response)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly transaction methods for year={}", year, e);
                return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                        .status("error")
                        .message("Failed to fetch monthly transaction methods for year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyMethod", "find_yearly_method", attrs, () -> {
            log.info("📊 Fetching yearly transaction methods until year={}", year);

            if (year == null || year < 1 || year > 9999) {
                log.error("❌ Invalid year provided: {}", year);
                return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                        .status("error")
                        .message("Invalid year provided")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionYearMethod> methods = transactionMethodRepository.findYearlyPaymentMethods(year);

                List<TransactionYearMethodResponse> response = methods.stream()
                        .map(TransactionYearMethodResponse::from)
                        .toList();

                log.info("✅ Retrieved {} yearly transaction method records until year={}", response.size(), year);

                return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                        .status("success")
                        .message("Successfully fetched yearly transaction methods until year=" + year)
                        .data(response)
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly transaction methods until year={}", year, e);
                return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                        .status("error")
                        .message("Failed to fetch yearly transaction methods until year=" + year)
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }
}
