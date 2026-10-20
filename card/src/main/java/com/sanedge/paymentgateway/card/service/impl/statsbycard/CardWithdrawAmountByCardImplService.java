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
import com.sanedge.paymentgateway.card.repository.statsbycard.CardWithdrawAmountByCardRepository;
import com.sanedge.paymentgateway.card.service.stats.withdraw.CardWithdrawAmountByCardService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardWithdrawAmountByCardImplService implements CardWithdrawAmountByCardService {
        private final CardWithdrawAmountByCardRepository cardWithdrawAmountByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountByCard(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_amount_by_card")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthAmountByCard", "find_month_amount_by_card", attrs, () -> {
                log.info("📊 Fetching monthly withdraw amount for card={} year={}", req.getCardNumber(), req.getYear());
                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardWithdrawAmountByCardRepository
                                        .getMonthlyWithdrawAmountByCard(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly withdraw records for card={} year={}",
                                        response.size(), req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly withdraw amounts retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly withdraw amounts for card={} year={}",
                                        req.getCardNumber(), req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly withdraw amounts")
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
                log.info("📊 Fetching yearly withdraw amount for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearAmount> amounts = cardWithdrawAmountByCardRepository
                                        .getYearlyWithdrawAmountByCard(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amounts.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} yearly withdraw records for card={} year={}",
                                        response.size(), req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly withdraw amounts retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly withdraw amounts for card={} year={}",
                                        req.getCardNumber(), req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly withdraw amounts")
                                        .data(null)
                                        .build();
                }
                });
        }
}
