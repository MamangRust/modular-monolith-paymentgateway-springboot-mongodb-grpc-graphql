package com.sanedge.paymentgateway.transfer.repository.statsbycard;

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
 * Status stats by card number. The SQL matched rows where the card was either
 * the sender ({@code transfer_from}) or the receiver ({@code transfer_to});
 * the $or in the $match keeps that semantics (Status enum values are stored by
 * NAME: 'SUCCESS' / 'FAILED').
 */
@Repository
public interface TransferStatusByCardRepository extends MongoRepository<Transfer, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'SUCCESS', $or: [ { transfer_from: ?0 }, { transfer_to: ?0 } ], transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferMonthStatusSuccess> aggregateFindMonthTransferStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferMonthStatusSuccess> findMonthTransferStatusSuccessByCard(String cardNumber, Long startYear,
            Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL only used startYear; endYear is kept for signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), startMonth, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(startYear.intValue(), endMonth, 1).plusMonths(1).atStartOfDay();
        return fillSuccessMonths(aggregateFindMonthTransferStatusSuccessByCard(cardNumber, start, end), startYear,
                startMonth, endMonth);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'FAILED', $or: [ { transfer_from: ?0 }, { transfer_to: ?0 } ], transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferMonthStatusFailed> aggregateFindMonthTransferStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferMonthStatusFailed> findMonthTransferStatusFailedByCard(String cardNumber, Long startYear,
            Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL only used startYear; endYear is kept for signature compatibility.
        LocalDateTime start = LocalDate.of(startYear.intValue(), startMonth, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(startYear.intValue(), endMonth, 1).plusMonths(1).atStartOfDay();
        return fillFailedMonths(aggregateFindMonthTransferStatusFailedByCard(cardNumber, start, end), startYear,
                startMonth, endMonth);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'SUCCESS', $or: [ { transfer_from: ?0 }, { transfer_to: ?0 } ], transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferYearStatusSuccess> aggregateFindYearlyTransferStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferYearStatusSuccess> findYearlyTransferStatusSuccessByCard(String cardNumber, Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillSuccessYears(aggregateFindYearlyTransferStatusSuccessByCard(cardNumber, start, start.plusYears(2)),
                year);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, status: 'FAILED', $or: [ { transfer_from: ?0 }, { transfer_to: ?0 } ], transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferYearStatusFailed> aggregateFindYearlyTransferStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferYearStatusFailed> findYearlyTransferStatusFailedByCard(String cardNumber, Long year) {
        // The SQL covered exactly two years (year and year-1), newest first.
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillFailedYears(aggregateFindYearlyTransferStatusFailedByCard(cardNumber, start, start.plusYears(2)),
                year);
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
