package com.sanedge.paymentgateway.saldo.service.impl.stats;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.saldo.domain.responses.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.paymentgateway.saldo.domain.responses.stats.balance.SaldoYearBalanceResponse;
import com.sanedge.paymentgateway.saldo.models.SaldoMonthBalance;
import com.sanedge.paymentgateway.saldo.models.SaldoYearBalance;
import com.sanedge.paymentgateway.saldo.repository.stats.SaldoBalanceRepository;
import com.sanedge.paymentgateway.saldo.service.stats.SaldoBalanceService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaldoBalanceImplService implements SaldoBalanceService {
    private final SaldoBalanceRepository saldoBalanceRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<SaldoMonthBalanceResponse>> getMonthBalance(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("getMonthBalance", "get_month_balance", attrs, () -> {
            try {
                log.info("📊 Fetching monthly saldo balance for year: {}", year);

                validateYear(year);

                List<SaldoMonthBalance> monthBalances = saldoBalanceRepository.findMonthlySaldoBalances(year);

                log.info("✅ Found {} monthly saldo balance records for year {}", monthBalances.size(), year);

                List<SaldoMonthBalanceResponse> response = monthBalances.stream()
                        .map(SaldoMonthBalanceResponse::from)
                        .toList();

                return ApiResponse.<List<SaldoMonthBalanceResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly saldo balance")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly saldo balance for year={}", year, e);
                return ApiResponse.<List<SaldoMonthBalanceResponse>>builder()
                        .status("error")
                        .message("Failed to fetch monthly saldo balance")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<SaldoYearBalanceResponse>> getYearBalance(Long year) {
        Attributes attrs = Attributes.builder()
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("getYearBalance", "get_year_balance", attrs, () -> {
            try {
                log.info("📊 Fetching yearly saldo balance for year: {}", year);

                validateYear(year);

                List<SaldoYearBalance> yearBalances = saldoBalanceRepository.findYearlySaldoBalances(year);

                log.info("✅ Found {} yearly saldo balance records for year {}", yearBalances.size(), year);

                List<SaldoYearBalanceResponse> response = yearBalances.stream()
                        .map(SaldoYearBalanceResponse::from)
                        .toList();

                return ApiResponse.<List<SaldoYearBalanceResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly saldo balance")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly saldo balance for year={}", year, e);
                return ApiResponse.<List<SaldoYearBalanceResponse>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly saldo balance")
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
