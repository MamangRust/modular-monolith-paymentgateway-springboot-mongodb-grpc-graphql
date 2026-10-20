package com.sanedge.paymentgateway.card.service.impl.statsbycard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;
import com.sanedge.paymentgateway.card.models.CardMonthAmount;
import com.sanedge.paymentgateway.card.models.CardYearAmount;
import com.sanedge.paymentgateway.card.repository.statsbycard.CardTopupAmountByCardRepository;
import com.sanedge.paymentgateway.card.service.stats.topup.CardTopupAmountByCardService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTopupAmountByCardImplService implements CardTopupAmountByCardService {
        private final CardTopupAmountByCardRepository cardTopupAmountByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountByCard(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_amount_by_card")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthAmountByCard", "find_month_amount_by_card", attrs, () -> {
                log.info("📊 Fetching monthly top-up stats for card={} year={}", req.getCardNumber(), req.getYear());

                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardTopupAmountByCardRepository
                                        .getMonthlyTopupAmountByCard(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly top-up records for card={} year={}",
                                        response.size(), req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly top-up stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly top-up for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly top-up stats")
                                        .data(null)
                                        .build();
                }
                });
        }

        @Override
        public ApiResponse<List<CardResponseYearAmount>> findYearAmountByCard(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_amount_by_card")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findYearAmountByCard", "find_year_amount_by_card", attrs, () -> {
                log.info("📊 Fetching yearly top-up stats for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearAmount> amount = cardTopupAmountByCardRepository
                                        .getYearlyTopupAmountByCard(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amount.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Yearly top-up for card={} year={} retrieved successfully",
                                        req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly top-up stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly top-up for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly top-up stats")
                                        .data(null)
                                        .build();
                }
                });
        }
}
