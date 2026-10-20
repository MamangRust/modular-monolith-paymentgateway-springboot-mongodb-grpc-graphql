package com.sanedge.paymentgateway.card.service.impl.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;
import com.sanedge.paymentgateway.card.models.CardMonthAmount;
import com.sanedge.paymentgateway.card.models.CardYearAmount;
import com.sanedge.paymentgateway.card.repository.stats.CardWithdrawAmountRepository;
import com.sanedge.paymentgateway.card.service.stats.withdraw.CardWithdrawAmountService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardWithdrawAmountImplService implements CardWithdrawAmountService {
    private final CardWithdrawAmountRepository cardWithdrawAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmount(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_month_amount")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthAmount", "find_month_amount", attrs, () -> {
        log.info("📊 Fetching monthly withdraw stats for year={}", year);

        validateYear(year);

        try {
            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> withdrawals = cardWithdrawAmountRepository.getMonthlyWithdrawAmount(yearDate);

            List<CardResponseMonthAmount> response = withdrawals.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly withdraw records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("Monthly withdraw stats retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch monthly withdraw for year={}", year, e);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly withdraw stats")
                    .data(null)
                    .build();
        }
        });
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmount(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_year_amount")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearAmount", "find_year_amount", attrs, () -> {
        log.info("📊 Fetching yearly withdraw stats for year={}", year);

        validateYear(year);

        try {
            List<CardYearAmount> result = cardWithdrawAmountRepository.getYearlyWithdrawAmount(year);

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Yearly withdraw for year={} retrieved successfully", year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("Yearly withdraw stats retrieved successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch yearly withdraw for year={}", year, e);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly withdraw stats")
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
