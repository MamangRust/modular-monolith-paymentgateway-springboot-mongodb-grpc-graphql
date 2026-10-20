package com.sanedge.paymentgateway.topup.service.impl.statsbycard;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.topup.domain.requests.stats.YearMonthMethod;
import com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupMonthMethodResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupYearlyMethodResponse;
import com.sanedge.paymentgateway.topup.models.TopupMonthMethod;
import com.sanedge.paymentgateway.topup.models.TopupYearMethod;
import com.sanedge.paymentgateway.topup.repository.statsbycard.TopupMethodByCardRepository;
import com.sanedge.paymentgateway.topup.service.stats.method.TopupMethodByCardService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupMethodByCardImplService implements TopupMethodByCardService {
    private final TopupMethodByCardRepository topupMethodByCardRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TopupMonthMethodResponse>> findMonthlyMethods(YearMonthMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();
        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyMethods", "find_monthly_methods", attrs, () -> {
            log.info("📊 Fetching monthly topup methods for card={} year={}", cardNumber, year);

            validateCard(cardNumber);
            validateYear(year);

            try {
                List<TopupMonthMethod> monthlyMethods = topupMethodByCardRepository
                        .findMonthlyTopupMethodsByCard(cardNumber, year);

                log.info("✅ Retrieved {} monthly topup methods for card={} year={}", monthlyMethods.size(), cardNumber,
                        year);

                List<TopupMonthMethodResponse> response = monthlyMethods.stream()
                        .map(TopupMonthMethodResponse::from)
                        .toList();

                return ApiResponse.<List<TopupMonthMethodResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched monthly topup methods for card=" + cardNumber)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly topup methods for card={} year={}", cardNumber, year, e);
                return ApiResponse.<List<TopupMonthMethodResponse>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch monthly topup methods for card=" + cardNumber)
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TopupYearlyMethodResponse>> findYearlyMethods(YearMonthMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();
        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyMethods", "find_yearly_methods", attrs, () -> {
            log.info("📊 Fetching yearly topup methods for card={} until year={}", cardNumber, year);

            validateCard(cardNumber);
            validateYear(year);

            try {
                List<TopupYearMethod> yearlyMethods = topupMethodByCardRepository
                        .findYearlyTopupMethodsByCardId(cardNumber, year);

                log.info("✅ Retrieved {} yearly topup methods for card={} until year={}", yearlyMethods.size(),
                        cardNumber, year);

                List<TopupYearlyMethodResponse> response = yearlyMethods.stream()
                        .map(TopupYearlyMethodResponse::from)
                        .toList();

                return ApiResponse.<List<TopupYearlyMethodResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Successfully fetched yearly topup methods for card=" + cardNumber)
                        .build();
            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly topup methods for card={} until year={}", cardNumber, year, e);
                return ApiResponse.<List<TopupYearlyMethodResponse>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .message("Failed to fetch yearly topup methods for card=" + cardNumber)
                        .build();
            }
        });
    }

    private void validateCard(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            log.error("❌ Card number is required");
            throw new IllegalArgumentException("Card number is required");
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
