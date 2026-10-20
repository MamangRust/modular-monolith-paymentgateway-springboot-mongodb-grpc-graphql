package com.sanedge.paymentgateway.withdraw.service.impl.statsbycard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.observability.TracingMetrics;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.MonthStatusWithdrawCardNumber;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.YearStatusWithdrawCardNumber;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusSuccess;
import com.sanedge.paymentgateway.withdraw.repository.statsbycard.WithdrawStatusByCardRepository;
import com.sanedge.paymentgateway.withdraw.service.stats.status.WithdrawStatusByCardService;

import io.opentelemetry.api.common.Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawStatusByCardImplService implements WithdrawStatusByCardService {
        private final WithdrawStatusByCardRepository withdrawStatusByCardRepository;
        private final TracingMetrics tracingMetrics;

        @Override
        public ApiResponse<List<WithdrawResponseMonthStatusSuccess>> findMonthStatusSuccessByCard(
                        MonthStatusWithdrawCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthStatusSuccessByCard", "find_month_success_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();
                                int month = req.getMonth();

                                log.info("📊 Fetching monthly SUCCESS withdraw status for card={}, year={}, month={}",
                                                cardNumber, year, month);

                                validateYearMonth(year, month);
                                validateCardNumber(cardNumber);

                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                List<WithdrawMonthStatusSuccess> results = withdrawStatusByCardRepository
                                                .findMonthWithdrawStatusSuccessByCard(cardNumber, year, month,
                                                                (long) nextMonth.getYear(), nextMonth.getMonthValue());

                                List<WithdrawResponseMonthStatusSuccess> response = results.stream()
                                                .map(WithdrawResponseMonthStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                                                .status("success")
                                                .message("Monthly SUCCESS withdraw status by card fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly SUCCESS withdraw status by card", e);
                                return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                                                .status("error")
                                                .message("Failed to fetch monthly SUCCESS withdraw status by card")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<WithdrawResponseYearStatusSuccess>> findYearlyStatusSuccessByCard(
                        YearStatusWithdrawCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyStatusSuccessByCard", "find_year_success_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();

                                log.info("📊 Fetching yearly SUCCESS withdraw status for card={}, year={}", cardNumber, year);

                                validateYear(year);
                                validateCardNumber(cardNumber);

                                List<WithdrawYearStatusSuccess> results = withdrawStatusByCardRepository
                                                .findYearlyWithdrawStatusSuccessByCard(cardNumber, year);

                                List<WithdrawResponseYearStatusSuccess> response = results.stream()
                                                .map(WithdrawResponseYearStatusSuccess::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                                                .status("success")
                                                .message("Yearly SUCCESS withdraw status by card fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly SUCCESS withdraw status by card", e);
                                return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                                                .status("error")
                                                .message("Failed to fetch yearly SUCCESS withdraw status by card")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<WithdrawResponseMonthStatusFailed>> findMonthStatusFailedByCard(
                        MonthStatusWithdrawCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .put("month", String.valueOf(req.getMonth()))
                                .build();

                return tracingMetrics.traceAndMeasure("findMonthStatusFailedByCard", "find_month_failed_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();
                                int month = req.getMonth();

                                log.info("📊 Fetching monthly FAILED withdraw status for card={}, year={}, month={}",
                                                cardNumber, year, month);

                                validateYearMonth(year, month);
                                validateCardNumber(cardNumber);

                                LocalDate currentMonth = LocalDate.of(year.intValue(), month, 1);
                                LocalDate nextMonth = currentMonth.plusMonths(1);

                                List<WithdrawMonthStatusFailed> results = withdrawStatusByCardRepository
                                                .findMonthWithdrawStatusFailedByCard(cardNumber, year, month,
                                                                (long) nextMonth.getYear(), nextMonth.getMonthValue());

                                List<WithdrawResponseMonthStatusFailed> response = results.stream()
                                                .map(WithdrawResponseMonthStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                                                .status("success")
                                                .message("Monthly FAILED withdraw status by card fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch monthly FAILED withdraw status by card", e);
                                return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                                                .status("error")
                                                .message("Failed to fetch monthly FAILED withdraw status by card")
                                                .data(null)
                                                .build();
                        }
                });
        }

        @Override
        public ApiResponse<List<WithdrawResponseYearStatusFailed>> findYearlyStatusFailedByCard(
                        YearStatusWithdrawCardNumber req) {
                Attributes attrs = Attributes.builder()
                                .put("cardNumber", req.getCardNumber() != null ? req.getCardNumber() : "")
                                .put("year", String.valueOf(req.getYear()))
                                .build();

                return tracingMetrics.traceAndMeasure("findYearlyStatusFailedByCard", "find_year_failed_by_card", attrs, () -> {
                        try {
                                String cardNumber = req.getCardNumber();
                                Long year = (long) req.getYear();

                                log.info("📊 Fetching yearly FAILED withdraw status for card={}, year={}", cardNumber, year);

                                validateYear(year);
                                validateCardNumber(cardNumber);

                                List<WithdrawYearStatusFailed> results = withdrawStatusByCardRepository
                                                .findYearlyWithdrawStatusFailedByCard(cardNumber, year);

                                List<WithdrawResponseYearStatusFailed> response = results.stream()
                                                .map(WithdrawResponseYearStatusFailed::from)
                                                .toList();

                                return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                                                .status("success")
                                                .message("Yearly FAILED withdraw status by card fetched successfully")
                                                .data(response)
                                                .build();

                        } catch (IllegalArgumentException e) {
                                log.warn("⚠️ Invalid input: {}", e.getMessage());
                                return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                                                .status("error")
                                                .message("invalid input")
                                                .data(null)
                                                .build();

                        } catch (Exception e) {
                                log.error("❌ Failed to fetch yearly FAILED withdraw status by card", e);
                                return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                                                .status("error")
                                                .message("Failed to fetch yearly FAILED withdraw status by card")
                                                .data(null)
                                                .build();
                        }
                });
        }

        private void validateYearMonth(Long year, Integer month) {
                if (year < 1 || year > 9999)
                        throw new IllegalArgumentException("Invalid year");
                if (month < 1 || month > 12)
                        throw new IllegalArgumentException("Invalid month");
        }

        private void validateYear(Long year) {
                if (year < 1 || year > 9999)
                        throw new IllegalArgumentException("Invalid year");
        }

        private void validateCardNumber(String cardNumber) {
                if (cardNumber == null || cardNumber.isBlank())
                        throw new IllegalArgumentException("Card number must not be null or blank");
        }
}
