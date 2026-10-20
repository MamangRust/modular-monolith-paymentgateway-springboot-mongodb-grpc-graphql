package com.sanedge.paymentgateway.transaction.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthYearPaymentMethod;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthMethod;
import com.sanedge.paymentgateway.transaction.models.TransactionYearMethod;
import com.sanedge.paymentgateway.transaction.repository.TransactionMethodByCardRepository;
import com.sanedge.paymentgateway.transaction.service.TransactionMethodByCardService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionMethodByCardImplService implements TransactionMethodByCardService {
    private final TransactionMethodByCardRepository transactionMethodByCardRepository;
    private final TracingMetrics tracingMetrics;

    @Override
    public ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(MonthYearPaymentMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findMonthlyMethodByCard", "find_monthly_method", attrs, () -> {
            log.info("📊 Fetching monthly transaction methods for cardNumber={}, year={}", cardNumber, year);

            if (!isValidYear(year) || !isValidCard(cardNumber)) {
                log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
                return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                        .status("error")
                        .message("Invalid input - year, cardNumber")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionMonthMethod> methods = transactionMethodByCardRepository
                        .findMonthlyPaymentMethodsByCard(cardNumber, year);

                List<TransactionMonthMethodResponse> response = methods.stream()
                        .map(TransactionMonthMethodResponse::from)
                        .toList();

                log.info("✅ Retrieved {} monthly transaction method records for cardNumber={}, year={}",
                        response.size(), cardNumber, year);

                return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Monthly Transactio methods retrieved successfully!")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch monthly transaction methods for cardNumber={}, year={}", cardNumber, year, e);
                return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                        .status("error")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    @Override
    public ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(MonthYearPaymentMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        Attributes attrs = Attributes.builder()
                .put("cardNumber", cardNumber != null ? cardNumber : "")
                .put("year", String.valueOf(year))
                .build();

        return tracingMetrics.traceAndMeasure("findYearlyMethodByCard", "find_yearly_method", attrs, () -> {
            log.info("📊 Fetching yearly transaction methods for cardNumber={}, until year={}", cardNumber, year);

            if (!isValidYear(year) || !isValidCard(cardNumber)) {
                log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
                return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                        .status("error")
                        .message("Invalid input - year, cardNumber")
                        .data(Collections.emptyList())
                        .build();
            }

            try {
                List<TransactionYearMethod> methods = transactionMethodByCardRepository
                        .findYearlyPaymentMethodsByCard(cardNumber, year);

                List<TransactionYearMethodResponse> response = methods.stream()
                        .map(TransactionYearMethodResponse::from)
                        .toList();

                log.info("✅ Retrieved {} yearly transaction method records for cardNumber={}, until year={}",
                        response.size(), cardNumber, year);

                return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                        .status("success")
                        .data(response)
                        .message("Yearly Transactio methods retrieved successfully!")
                        .build();

            } catch (Exception e) {
                log.error("💥 Failed to fetch yearly transaction methods for cardNumber={}, year={}", cardNumber, year, e);
                return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                        .status("error")
                        .message("Failed to fetch yearly transaction methods for cardNumber")
                        .data(Collections.emptyList())
                        .build();
            }
        });
    }

    private boolean isValidYear(Long year) {
        return year != null && year >= 1 && year <= 9999;
    }

    private boolean isValidCard(String cardNumber) {
        return cardNumber != null && !cardNumber.isBlank();
    }
}
