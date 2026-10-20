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
import com.sanedge.paymentgateway.card.repository.statsbycard.CardTransferAmountByCardRepository;
import com.sanedge.paymentgateway.card.service.stats.transfer.CardTransferAmountByCardService;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTransferAmountByCardImplService implements CardTransferAmountByCardService {
        private final CardTransferAmountByCardRepository cardTransferAmountByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_amount_sender")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthAmountSender", "find_month_amount_sender", attrs, () -> {
                log.info("📊 Fetching monthly transfer (sent) for card={} year={}", req.getCardNumber(), req.getYear());
                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardTransferAmountByCardRepository
                                        .getMonthlyTransferAmountBySender(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly transfer (sent) records", response.size());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly transfer (sent) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly transfer (sent) for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly transfer (sent)")
                                        .data(null)
                                        .build();
                }
                });
        }

        @Override
        public ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_amount_sender")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findYearAmountSender", "find_year_amount_sender", attrs, () -> {
                log.info("📊 Fetching yearly transfer (sent) for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearAmount> amounts = cardTransferAmountByCardRepository
                                        .getYearlyTransferAmountBySender(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amounts.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Yearly transfer (sent) retrieved successfully for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly transfer (sent) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly transfer (sent) for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly transfer (sent)")
                                        .data(null)
                                        .build();
                }
                });
        }

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_month_amount_receiver")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthAmountReceiver", "find_month_amount_receiver", attrs, () -> {
                log.info("📊 Fetching monthly transfer (received) for card={} year={}", req.getCardNumber(),
                                req.getYear());
                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardTransferAmountByCardRepository
                                        .getMonthlyTransferAmountByReceiver(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly transfer (received) records", response.size());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly transfer (received) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly transfer (received) for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly transfer (received)")
                                        .data(null)
                                        .build();
                }
                });
        }

        @Override
        public ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(MonthYearCardNumberCard req) {
                Attributes attrs = Attributes.builder()
                                .put("operation", "find_year_amount_receiver")
                                .put("stats.year", String.valueOf(req.getYear()))
                                .put("stats.card", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .build();

                return tracingMetrics.traceAndMeasure("findYearAmountReceiver", "find_year_amount_receiver", attrs, () -> {
                log.info("📊 Fetching yearly transfer (received) for card={} year={}", req.getCardNumber(),
                                req.getYear());

                try {
                        List<CardYearAmount> amounts = cardTransferAmountByCardRepository
                                        .getYearlyTransferAmountByReceiver(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amounts.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Yearly transfer (received) retrieved successfully for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly transfer (received) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly transfer (received) for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly transfer (received)")
                                        .data(null)
                                        .build();
                }
                });
        }
}
