package com.sanedge.paymentgateway.transfer.repository.stats;

import com.sanedge.paymentgateway.transfer.models.Transfer;
import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.models.TransferYearStatusFailed;
import com.sanedge.paymentgateway.transfer.models.TransferYearStatusSuccess;

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
 * Status stats. The SQL used a months CTE + SUM(CASE WHEN status = 'success'|'failed'
 * ...), i.e. only rows of that status were counted and summed; the $match keeps
 * that semantics (Status enum values are stored by NAME: 'SUCCESS' / 'FAILED').
 */
@Repository
public interface TransferStatusRepository extends MongoRepository<Transfer, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'SUCCESS', transfer_time: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferMonthStatusSuccess> aggregateFindMonthTransferStatusSuccess(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransferMonthStatusSuccess> findMonthTransferStatusSuccess(Long startYear, Integer startMonth,
            Long endYear, Integer endMonth) {
        // The SQL only used startYear; endYear is kept for signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), startMonth, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(startYear.intValue(), endMonth, 1).plusMonths(1).atStartOfDay();
        return fillSuccessMonths(aggregateFindMonthTransferStatusSuccess(start, end), startYear, startMonth, endMonth);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'FAILED', transfer_time: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferMonthStatusFailed> aggregateFindMonthTransferStatusFailed(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransferMonthStatusFailed> findMonthTransferStatusFailed(Long startYear, Integer startMonth,
            Long endYear, Integer endMonth) {
        // The SQL only used startYear; endYear is kept for signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), startMonth, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(startYear.intValue(), endMonth, 1).plusMonths(1).atStartOfDay();
        return fillFailedMonths(aggregateFindMonthTransferStatusFailed(start, end), startYear, startMonth, endMonth);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'SUCCESS', transfer_time: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferYearStatusSuccess> aggregateFindYearlyTransferStatusSuccess(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransferYearStatusSuccess> findYearlyTransferStatusSuccess(Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillSuccessYears(aggregateFindYearlyTransferStatusSuccess(start, start.plusYears(2)), year);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'FAILED', transfer_time: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferYearStatusFailed> aggregateFindYearlyTransferStatusFailed(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransferYearStatusFailed> findYearlyTransferStatusFailed(Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillFailedYears(aggregateFindYearlyTransferStatusFailed(start, start.plusYears(2)), year);
    }

    private List<TransferMonthStatusSuccess> fillSuccessMonths(List<TransferMonthStatusSuccess> rows, long startYear,
            int startMonth, int endMonth) {
        Map<String, TransferMonthStatusSuccess> byMonth = rows.stream()
                .collect(Collectors.toMap(TransferMonthStatusSuccess::getMonthName, Function.identity(), (a, b) -> a));
        List<TransferMonthStatusSuccess> result = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            String monthName = MONTHS[m - 1];
            TransferMonthStatusSuccess row = byMonth.get(monthName);
            result.add(row != null
                    ? new TransferMonthStatusSuccess(String.valueOf(startYear), monthName, row.getTotalSuccess(),
                            row.getTotalAmount())
                    : new TransferMonthStatusSuccess(String.valueOf(startYear), monthName, 0L, 0L));
        }
        return result;
    }

    private List<TransferMonthStatusFailed> fillFailedMonths(List<TransferMonthStatusFailed> rows, long startYear,
            int startMonth, int endMonth) {
        Map<String, TransferMonthStatusFailed> byMonth = rows.stream()
                .collect(Collectors.toMap(TransferMonthStatusFailed::getMonthName, Function.identity(), (a, b) -> a));
        List<TransferMonthStatusFailed> result = new ArrayList<>();
        for (int m = startMonth; m <= endMonth; m++) {
            String monthName = MONTHS[m - 1];
            TransferMonthStatusFailed row = byMonth.get(monthName);
            result.add(row != null
                    ? new TransferMonthStatusFailed(String.valueOf(startYear), monthName, row.getTotalFailed(),
                            row.getTotalAmount())
                    : new TransferMonthStatusFailed(String.valueOf(startYear), monthName, 0L, 0L));
        }
        return result;
    }

    private List<TransferYearStatusSuccess> fillSuccessYears(List<TransferYearStatusSuccess> rows, long year) {
        Map<String, TransferYearStatusSuccess> byYear = rows.stream()
                .collect(Collectors.toMap(TransferYearStatusSuccess::getReportYear, Function.identity(), (a, b) -> a));
        List<TransferYearStatusSuccess> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            TransferYearStatusSuccess row = byYear.get(String.valueOf(y));
            result.add(row != null
                    ? new TransferYearStatusSuccess(String.valueOf(y), row.getTotalSuccess(), row.getTotalAmount())
                    : new TransferYearStatusSuccess(String.valueOf(y), 0L, 0L));
        }
        return result;
    }

    private List<TransferYearStatusFailed> fillFailedYears(List<TransferYearStatusFailed> rows, long year) {
        Map<String, TransferYearStatusFailed> byYear = rows.stream()
                .collect(Collectors.toMap(TransferYearStatusFailed::getReportYear, Function.identity(), (a, b) -> a));
        List<TransferYearStatusFailed> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            TransferYearStatusFailed row = byYear.get(String.valueOf(y));
            result.add(row != null
                    ? new TransferYearStatusFailed(String.valueOf(y), row.getTotalFailed(), row.getTotalAmount())
                    : new TransferYearStatusFailed(String.valueOf(y), 0L, 0L));
        }
        return result;
    }
}
