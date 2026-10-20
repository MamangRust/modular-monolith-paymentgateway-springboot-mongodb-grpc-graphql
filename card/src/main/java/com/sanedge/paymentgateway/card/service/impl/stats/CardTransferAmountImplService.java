package com.sanedge.paymentgateway.card.service.impl.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;
import com.sanedge.paymentgateway.card.models.CardMonthAmount;
import com.sanedge.paymentgateway.card.models.CardYearAmount;
import com.sanedge.paymentgateway.card.repository.stats.CardTransferAmountRepository;
import com.sanedge.paymentgateway.card.service.stats.transfer.CardTransferAmountService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTransferAmountImplService implements CardTransferAmountService {
    private final CardTransferAmountRepository cardTransferAmountRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_month_amount_sender")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthAmountSender", "find_month_amount_sender", attrs, () -> {
        log.info("📊 Fetching monthly transfer sender stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> transfers = cardTransferAmountRepository.getMonthlyTransferAmountSender(yearDate);

            if (transfers.isEmpty()) {
                log.warn("⚠️ No monthly transfer sender data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly transfer sender stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseMonthAmount> response = transfers.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transfer sender records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly transfer sender stats retrieved successfully!")
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
            log.error("❌ Failed to fetch monthly transfer sender for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transfer sender stats")
                    .data(List.of())
                    .build();
        }
        });
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_year_amount_sender")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearAmountSender", "find_year_amount_sender", attrs, () -> {
        log.info("📊 Fetching yearly transfer sender stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTransferAmountRepository.getYearlyTransferAmountSender(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly transfer sender data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly transfer sender stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transfer sender records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly transfer sender stats retrieved successfully!")
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
            log.error("❌ Failed to fetch yearly transfer sender for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transfer sender stats")
                    .data(List.of())
                    .build();
        }
        });
    }

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_month_amount_receiver")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthAmountReceiver", "find_month_amount_receiver", attrs, () -> {
        log.info("📊 Fetching monthly transfer receiver stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> transfers = cardTransferAmountRepository.getMonthlyTransferAmountReceiver(yearDate);

            if (transfers.isEmpty()) {
                log.warn("⚠️ No monthly transfer receiver data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly transfer receiver stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseMonthAmount> response = transfers.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transfer receiver records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly transfer receiver stats retrieved successfully!")
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
            log.error("❌ Failed to fetch monthly transfer receiver for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transfer receiver stats")
                    .data(List.of())
                    .build();
        }
        });
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(Long year) {
        Attributes attrs = Attributes.builder()
                .put("operation", "find_year_amount_receiver")
                .put("stats.year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearAmountReceiver", "find_year_amount_receiver", attrs, () -> {
        log.info("📊 Fetching yearly transfer receiver stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTransferAmountRepository.getYearlyTransferAmountReceiver(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly transfer receiver data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly transfer receiver stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transfer receiver records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly transfer receiver stats retrieved successfully!")
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
            log.error("❌ Failed to fetch yearly transfer receiver for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transfer receiver stats")
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
