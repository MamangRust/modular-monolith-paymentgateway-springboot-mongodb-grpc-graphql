package com.sanedge.paymentgateway.card.service.impl.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;
import com.sanedge.paymentgateway.card.models.CardMonthAmount;
import com.sanedge.paymentgateway.card.models.CardYearAmount;
import com.sanedge.paymentgateway.card.repository.stats.CardTransactionAmountRepository;
import com.sanedge.paymentgateway.card.service.stats.transaction.CardTransactionAmountService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTransactionAmountImplService implements CardTransactionAmountService {
    private final CardTransactionAmountRepository cardTransactionAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmount(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_month_amount")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthAmount", "find_month_amount", attrs, () -> {
        log.info("📊 Fetching monthly transaction stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> transactions = cardTransactionAmountRepository.getMonthlyTransactionAmount(yearDate);

            if (transactions.isEmpty()) {
                log.warn("⚠️ No monthly transaction data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly transaction stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseMonthAmount> response = transactions.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transaction records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly transaction stats retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid year: {}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(List.of())
                    .build();
        } catch (Exception e) {
            log.error("❌ Failed to fetch monthly transaction for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transaction stats")
                    .data(List.of())
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
        log.info("📊 Fetching yearly transaction stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTransactionAmountRepository.getYearlyTransactionAmount(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly transaction data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly transaction stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transaction records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly transaction stats retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid year: {}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(List.of())
                    .build();
        } catch (Exception e) {
            log.error("❌ Failed to fetch yearly transaction for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transaction stats")
                    .data(List.of())
                    .build();
        }
        });
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
    }
}
