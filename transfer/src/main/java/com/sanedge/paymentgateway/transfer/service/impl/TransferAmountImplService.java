package com.sanedge.paymentgateway.transfer.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse;
import com.sanedge.paymentgateway.transfer.models.TransferMonthAmount;
import com.sanedge.paymentgateway.transfer.models.TransferYearAmount;
import com.sanedge.paymentgateway.transfer.repository.stats.TransferAmountRepository;
import com.sanedge.paymentgateway.transfer.service.TransferAmountService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferAmountImplService implements TransferAmountService {
    private final TransferAmountRepository transferAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmounts(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyTransferAmounts", "find_monthly_amounts", attrs, () -> {
            log.info("📊 Fetching monthly transfer amounts for year={}", year);

            validateYear(year);

            try {
                List<TransferMonthAmount> amounts = transferAmountRepository.findMonthlyTransferAmounts(year);

                log.info("✅ Retrieved {} monthly transfer amount records for year={}", amounts.size(), year);

                List<TransferMonthAmountResponse> response = amounts.stream()
                        .map(TransferMonthAmountResponse::from)
                        .toList();

                return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                        .status("success")
                        .message("Monthly transfer amounts retrieved successfully!")
                        .data(response)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to fetch monthly transfer amounts for year={}", year);

                return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                        .status("error")
                        .message("Failed to retrieve monthly transfer amounts")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransferYearAmountResponse>> findYearlyAmounts(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyTransferAmounts", "find_yearly_amounts", attrs, () -> {
            log.info("📊 Fetching yearly transfer amounts until year={}", year);

            validateYear(year);

            try {
                List<TransferYearAmount> amounts = transferAmountRepository.findYearlyTransferAmounts(year);

                log.info("✅ Retrieved {} yearly transfer amount records until year={}", amounts.size(), year);

                List<TransferYearAmountResponse> response = amounts.stream()
                        .map(TransferYearAmountResponse::from)
                        .toList();

                return ApiResponse.<List<TransferYearAmountResponse>>builder()
                        .status("success")
                        .message("Yearly transfer amounts retrieved successfully!")
                        .data(response)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to fetch yearly transfer amounts until year={}", year);

                return ApiResponse.<List<TransferYearAmountResponse>>builder()
                        .status("error")
                        .message("Failed to retrieve yearly transfer amounts")
                        .data(null)
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
