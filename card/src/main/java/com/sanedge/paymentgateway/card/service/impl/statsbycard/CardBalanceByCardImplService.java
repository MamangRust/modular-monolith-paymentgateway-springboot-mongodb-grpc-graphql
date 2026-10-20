package com.sanedge.paymentgateway.card.service.impl.statsbycard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseMonthBalance;
import com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseYearBalance;
import com.sanedge.paymentgateway.card.models.CardMonthBalance;
import com.sanedge.paymentgateway.card.models.CardYearBalance;
import com.sanedge.paymentgateway.card.repository.statsbycard.CardBalanceByCardRepository;
import com.sanedge.paymentgateway.card.service.stats.balance.CardBalanceByCardService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardBalanceByCardImplService implements CardBalanceByCardService {
        private final CardBalanceByCardRepository cardBalanceByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<CardResponseMonthBalance>> findMonthBalanceByCard(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_balance_by_card")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthBalanceByCard", "find_month_balance_by_card", attrs, () -> {
                log.info("📊 Fetching monthly balance stats for card={} year={}", req.getCardNumber(), req.getYear());

                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthBalance> balances = cardBalanceByCardRepository.getMonthlyBalancesByCard(yearDate,
                                        req.getCardNumber());

                        List<CardResponseMonthBalance> response = balances.stream()
                                        .map(CardResponseMonthBalance::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly balance records for card={} year={}",
                                        response.size(), req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseMonthBalance>>builder()
                                        .status("success")
                                        .message("Monthly balance stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly balance for card={} year={}",
                                        req.getCardNumber(), req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthBalance>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly balance stats")
                                        .data(null)
                                        .build();
                }
                });
        }

        @Override
        public ApiResponse<List<CardResponseYearBalance>> findYearBalanceByCard(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_balance_by_card")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findYearBalanceByCard", "find_year_balance_by_card", attrs, () -> {
                log.info("📊 Fetching yearly balance stats for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearBalance> balance = cardBalanceByCardRepository.getYearlyBalancesByCard(
                                        req.getYear(),
                                        req.getCardNumber());

                        List<CardResponseYearBalance> response = balance.stream()
                                        .map(CardResponseYearBalance::from)
                                        .toList();

                        log.info("✅ Yearly balance for card={} year={} retrieved successfully",
                                        req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseYearBalance>>builder()
                                        .status("success")
                                        .message("Yearly balance stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly balance for card={} year={}",
                                        req.getCardNumber(), req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearBalance>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly balance stats")
                                        .data(null)
                                        .build();
                }
                });
        }
}
