package com.sanedge.paymentgateway.topup.repository.statsbycard;

import com.sanedge.paymentgateway.topup.models.Topup;
import com.sanedge.paymentgateway.topup.models.TopupMonthStatusFailed;
import com.sanedge.paymentgateway.topup.models.TopupMonthStatusSuccess;
import com.sanedge.paymentgateway.topup.models.TopupYearStatusFailed;
import com.sanedge.paymentgateway.topup.models.TopupYearStatusSuccess;

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
 * status = 'success'|'failed' ...) filtered by card_number, i.e. only rows of
 * that status were counted and summed; the $match keeps that semantics (Status
 * enum values are stored by NAME: 'SUCCESS' / 'FAILED'). The monthly SQL
 * grouped all 12 months of startYear; the yearly SQL covered exactly two years
 * (year and year-1), newest first.
 */
@Repository
public interface TopupStatusByCardRepository extends MongoRepository<Topup, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'SUCCESS', topup_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$topup_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupMonthStatusSuccess> aggregateFindMonthTopupStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TopupMonthStatusSuccess> findMonthTopupStatusSuccessByCard(String cardNumber, Long startYear,
            Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL only used startYear; startMonth/endYear/endMonth are kept for
        // signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), 1, 1).atStartOfDay();
        return fillSuccessMonths(aggregateFindMonthTopupStatusSuccessByCard(cardNumber, start, start.plusYears(1)),
                startYear);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'FAILED', topup_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$topup_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupMonthStatusFailed> aggregateFindMonthTopupStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TopupMonthStatusFailed> findMonthTopupStatusFailedByCard(String cardNumber, Long startYear,
            Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL only used startYear; startMonth/endYear/endMonth are kept for
        // signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), 1, 1).atStartOfDay();
        return fillFailedMonths(aggregateFindMonthTopupStatusFailedByCard(cardNumber, start, start.plusYears(1)),
                startYear);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'SUCCESS', topup_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$topup_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupYearStatusSuccess> aggregateFindYearlyTopupStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TopupYearStatusSuccess> findYearlyTopupStatusSuccessByCard(String cardNumber, Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillSuccessYears(aggregateFindYearlyTopupStatusSuccessByCard(cardNumber, start, start.plusYears(2)),
                year);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'FAILED', topup_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$topup_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupYearStatusFailed> aggregateFindYearlyTopupStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TopupYearStatusFailed> findYearlyTopupStatusFailedByCard(String cardNumber, Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillFailedYears(aggregateFindYearlyTopupStatusFailedByCard(cardNumber, start, start.plusYears(2)),
                year);
    }

    private List<TopupMonthStatusSuccess> fillSuccessMonths(List<TopupMonthStatusSuccess> rows, long startYear) {
        Map<String, TopupMonthStatusSuccess> byMonth = rows.stream()
                .collect(Collectors.toMap(TopupMonthStatusSuccess::getMonthName, Function.identity(), (a, b) -> a));
        List<TopupMonthStatusSuccess> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            TopupMonthStatusSuccess row = byMonth.get(month);
            result.add(row != null
                    ? new TopupMonthStatusSuccess(String.valueOf(startYear), month, row.getTotalSuccess(),
                            row.getTotalAmount())
                    : new TopupMonthStatusSuccess(String.valueOf(startYear), month, 0L, 0L));
        }
        return result;
    }

    private List<TopupMonthStatusFailed> fillFailedMonths(List<TopupMonthStatusFailed> rows, long startYear) {
        Map<String, TopupMonthStatusFailed> byMonth = rows.stream()
                .collect(Collectors.toMap(TopupMonthStatusFailed::getMonthName, Function.identity(), (a, b) -> a));
        List<TopupMonthStatusFailed> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            TopupMonthStatusFailed row = byMonth.get(month);
            result.add(row != null
                    ? new TopupMonthStatusFailed(String.valueOf(startYear), month, row.getTotalFailed(),
                            row.getTotalAmount())
                    : new TopupMonthStatusFailed(String.valueOf(startYear), month, 0L, 0L));
        }
        return result;
    }

    private List<TopupYearStatusSuccess> fillSuccessYears(List<TopupYearStatusSuccess> rows, long year) {
        Map<String, TopupYearStatusSuccess> byYear = rows.stream()
                .collect(Collectors.toMap(TopupYearStatusSuccess::getReportYear, Function.identity(), (a, b) -> a));
        List<TopupYearStatusSuccess> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            TopupYearStatusSuccess row = byYear.get(String.valueOf(y));
            result.add(row != null
                    ? new TopupYearStatusSuccess(String.valueOf(y), row.getTotalSuccess(), row.getTotalAmount())
                    : new TopupYearStatusSuccess(String.valueOf(y), 0L, 0L));
        }
        return result;
    }

    private List<TopupYearStatusFailed> fillFailedYears(List<TopupYearStatusFailed> rows, long year) {
        Map<String, TopupYearStatusFailed> byYear = rows.stream()
                .collect(Collectors.toMap(TopupYearStatusFailed::getReportYear, Function.identity(), (a, b) -> a));
        List<TopupYearStatusFailed> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            TopupYearStatusFailed row = byYear.get(String.valueOf(y));
            result.add(row != null
                    ? new TopupYearStatusFailed(String.valueOf(y), row.getTotalFailed(), row.getTotalAmount())
                    : new TopupYearStatusFailed(String.valueOf(y), 0L, 0L));
        }
        return result;
    }
}
