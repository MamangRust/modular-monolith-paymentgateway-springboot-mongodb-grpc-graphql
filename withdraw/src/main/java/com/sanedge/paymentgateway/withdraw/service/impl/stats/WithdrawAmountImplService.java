package com.sanedge.paymentgateway.withdraw.service.impl.stats;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthlyAmount;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearlyAmount;
import com.sanedge.paymentgateway.withdraw.repository.stats.WithdrawAmountRepository;
import com.sanedge.paymentgateway.withdraw.service.stats.amount.WithdrawAmountService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawAmountImplService implements WithdrawAmountService {

    private final WithdrawAmountRepository withdrawAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<WithdrawMonthlyAmountResponse>> findMonthlyWithdraws(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyWithdrawsAmount", "find_monthly_amount", attrs, () -> {
            log.info("📊 Fetching monthly withdraw amounts for year={}", year);

            try {
                validateYear(year);

                List<WithdrawMonthlyAmount> amounts = withdrawAmountRepository.findMonthlyWithdraws(year);

                log.info("✅ Retrieved {} monthly withdraw amount records for year={}", amounts.size(), year);

                List<WithdrawMonthlyAmountResponse> response = amounts.stream()
                        .map(WithdrawMonthlyAmountResponse::from)
                        .toList();

                return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                        .status("success")
                        .message("Monthly withdraw amounts retrieved successfully!")
                        .data(response)
                        .build();

            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Invalid input year={}", year);
                return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                        .status("error")
                        .message("Invalid year provided")
                        .data(null)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to fetch monthly withdraw amounts for year={}", year, e);
                return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                        .status("error")
                        .message("Failed to retrieve monthly withdraw amounts")
                        .data(null)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<WithdrawYearlyAmountResponse>> findYearlyWithdraws(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyWithdrawsAmount", "find_yearly_amount", attrs, () -> {
            log.info("📊 Fetching yearly withdraw amounts until year={}", year);

            try {
                validateYear(year);

                List<WithdrawYearlyAmount> amounts = withdrawAmountRepository.findYearlyWithdraws(year);

                log.info("✅ Retrieved {} yearly withdraw amount records until year={}", amounts.size(), year);

                List<WithdrawYearlyAmountResponse> response = amounts.stream()
                        .map(WithdrawYearlyAmountResponse::from)
                        .toList();

                return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                        .status("success")
                        .message("Yearly withdraw amounts retrieved successfully!")
                        .data(response)
                        .build();

            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Invalid input year={}", year);
                return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                        .status("error")
                        .message("Invalid year provided")
                        .data(null)
                        .build();

            } catch (Exception e) {
                log.error("❌ Failed to fetch yearly withdraw amounts until year={}", year, e);
                return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                        .status("error")
                        .message("Failed to retrieve yearly withdraw amounts")
                        .data(null)
                        .build();
            }
        });
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
