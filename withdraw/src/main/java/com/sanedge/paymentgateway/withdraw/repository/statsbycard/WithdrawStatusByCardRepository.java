package com.sanedge.paymentgateway.withdraw.repository.statsbycard;

import com.sanedge.paymentgateway.withdraw.models.Withdraw;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusSuccess;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Per-card status stats. The SQL used LEFT JOIN + SUM(CASE WHEN
 * status = 'success'|'failed' ...), i.e. only rows of that status were counted
 * and summed; the $match keeps that semantics (Status enum values are stored by
 * NAME: 'SUCCESS' / 'FAILED').
 */
@Repository
public interface WithdrawStatusByCardRepository extends MongoRepository<Withdraw, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'SUCCESS', withdraw_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$withdraw_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<WithdrawMonthStatusSuccess> aggregateFindMonthWithdrawStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<WithdrawMonthStatusSuccess> findMonthWithdrawStatusSuccessByCard(String cardNumber, Long startYear,
            Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL only used startYear; endYear is kept for signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), startMonth, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(startYear.intValue(), endMonth, 1).plusMonths(1).atStartOfDay();
        return fillSuccessMonths(aggregateFindMonthWithdrawStatusSuccessByCard(cardNumber, start, end), startYear, startMonth,
                endMonth);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'FAILED', withdraw_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$withdraw_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<WithdrawMonthStatusFailed> aggregateFindMonthWithdrawStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<WithdrawMonthStatusFailed> findMonthWithdrawStatusFailedByCard(String cardNumber, Long startYear,
            Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL only used startYear; endYear is kept for signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), startMonth, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(startYear.intValue(), endMonth, 1).plusMonths(1).atStartOfDay();
        return fillFailedMonths(aggregateFindMonthWithdrawStatusFailedByCard(cardNumber, start, end), startYear, startMonth,
                endMonth);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'SUCCESS', withdraw_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$withdraw_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<WithdrawYearStatusSuccess> aggregateFindYearlyWithdrawStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<WithdrawYearStatusSuccess> findYearlyWithdrawStatusSuccessByCard(String cardNumber, Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillSuccessYears(aggregateFindYearlyWithdrawStatusSuccessByCard(cardNumber, start, start.plusYears(2)), year);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'FAILED', withdraw_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$withdraw_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<WithdrawYearStatusFailed> aggregateFindYearlyWithdrawStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<WithdrawYearStatusFailed> findYearlyWithdrawStatusFailedByCard(String cardNumber, Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillFailedYears(aggregateFindYearlyWithdrawStatusFailedByCard(cardNumber, start, start.plusYears(2)), year);
    }

    private List<WithdrawMonthStatusSuccess> fillSuccessMonths(List<WithdrawMonthStatusSuccess> rows, long startYear,
            int startMonth, int endMonth) {
        Map<String, WithdrawMonthStatusSuccess> byMonth = rows.stream()
                .collect(Collectors.toMap(WithdrawMonthStatusSuccess::getMonthName, Function.identity(), (a, b) -> a));
        List<WithdrawMonthStatusSuccess> result = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            String monthName = MONTHS[m - 1];
            WithdrawMonthStatusSuccess row = byMonth.get(monthName);
            result.add(row != null
                    ? new WithdrawMonthStatusSuccess(String.valueOf(startYear), monthName, row.getTotalSuccess(),
                            row.getTotalAmount())
                    : new WithdrawMonthStatusSuccess(String.valueOf(startYear), monthName, 0L, 0L));
        }
        return result;
    }

    private List<WithdrawMonthStatusFailed> fillFailedMonths(List<WithdrawMonthStatusFailed> rows, long startYear,
            int startMonth, int endMonth) {
        Map<String, WithdrawMonthStatusFailed> byMonth = rows.stream()
                .collect(Collectors.toMap(WithdrawMonthStatusFailed::getMonthName, Function.identity(), (a, b) -> a));
        List<WithdrawMonthStatusFailed> result = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            String monthName = MONTHS[m - 1];
            WithdrawMonthStatusFailed row = byMonth.get(monthName);
            result.add(row != null
                    ? new WithdrawMonthStatusFailed(String.valueOf(startYear), monthName, row.getTotalFailed(),
                            row.getTotalAmount())
                    : new WithdrawMonthStatusFailed(String.valueOf(startYear), monthName, 0L, 0L));
        }
        return result;
    }

    private List<WithdrawYearStatusSuccess> fillSuccessYears(List<WithdrawYearStatusSuccess> rows, long year) {
        Map<String, WithdrawYearStatusSuccess> byYear = rows.stream()
                .collect(Collectors.toMap(WithdrawYearStatusSuccess::getReportYear, Function.identity(), (a, b) -> a));
        List<WithdrawYearStatusSuccess> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            WithdrawYearStatusSuccess row = byYear.get(String.valueOf(y));
            result.add(row != null
                    ? new WithdrawYearStatusSuccess(String.valueOf(y), row.getTotalSuccess(), row.getTotalAmount())
                    : new WithdrawYearStatusSuccess(String.valueOf(y), 0L, 0L));
        }
        return result;
    }

    private List<WithdrawYearStatusFailed> fillFailedYears(List<WithdrawYearStatusFailed> rows, long year) {
        Map<String, WithdrawYearStatusFailed> byYear = rows.stream()
                .collect(Collectors.toMap(WithdrawYearStatusFailed::getReportYear, Function.identity(), (a, b) -> a));
        List<WithdrawYearStatusFailed> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            WithdrawYearStatusFailed row = byYear.get(String.valueOf(y));
            result.add(row != null
                    ? new WithdrawYearStatusFailed(String.valueOf(y), row.getTotalFailed(), row.getTotalAmount())
                    : new WithdrawYearStatusFailed(String.valueOf(y), 0L, 0L));
        }
        return result;
    }
}
