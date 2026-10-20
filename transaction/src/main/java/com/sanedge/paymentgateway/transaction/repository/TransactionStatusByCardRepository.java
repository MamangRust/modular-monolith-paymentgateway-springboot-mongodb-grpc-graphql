package com.sanedge.paymentgateway.transaction.repository;

import com.sanedge.paymentgateway.transaction.models.Transaction;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusFailed;
import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusSuccess;

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

@Repository
public interface TransactionStatusByCardRepository extends MongoRepository<Transaction, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'SUCCESS', transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { month: { $month: '$transaction_time' }, year: { $year: '$transaction_time' } }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id.year' }, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id.month', 1] }] }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionMonthStatusSuccess> aggregateFindMonthTransactionStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransactionMonthStatusSuccess> findMonthTransactionStatusSuccessByCard(String cardNumber,
            Long startYear, Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL ignores startMonth/endMonth and reports all 12 months of startYear.
        LocalDateTime start = LocalDate.of(startYear.intValue(), 1, 1).atStartOfDay();
        return fillSuccessMonths(aggregateFindMonthTransactionStatusSuccessByCard(cardNumber, start, start.plusYears(1)),
                startYear.intValue());
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'FAILED', transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { month: { $month: '$transaction_time' }, year: { $year: '$transaction_time' } }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id.year' }, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id.month', 1] }] }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionMonthStatusFailed> aggregateFindMonthTransactionStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransactionMonthStatusFailed> findMonthTransactionStatusFailedByCard(String cardNumber,
            Long startYear, Integer startMonth, Long endYear, Integer endMonth) {
        // The SQL ignores startMonth/endMonth and reports all 12 months of startYear.
        LocalDateTime start = LocalDate.of(startYear.intValue(), 1, 1).atStartOfDay();
        return fillFailedMonths(aggregateFindMonthTransactionStatusFailedByCard(cardNumber, start, start.plusYears(1)),
                startYear.intValue());
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'SUCCESS', transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transaction_time' }, totalSuccess: { $sum: 1 }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalSuccess: { $ifNull: ['$totalSuccess', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionYearStatusSuccess> aggregateFindYearlyTransactionStatusSuccessByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransactionYearStatusSuccess> findYearlyTransactionStatusSuccessByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillSuccessYears(aggregateFindYearlyTransactionStatusSuccessByCard(cardNumber, start, start.plusYears(2)),
                year, 2, true);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, status: 'FAILED', transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transaction_time' }, totalFailed: { $sum: 1 }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalFailed: { $ifNull: ['$totalFailed', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionYearStatusFailed> aggregateFindYearlyTransactionStatusFailedByCard(String cardNumber,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransactionYearStatusFailed> findYearlyTransactionStatusFailedByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillFailedYears(aggregateFindYearlyTransactionStatusFailedByCard(cardNumber, start, start.plusYears(2)),
                year, 2, true);
    }

    private List<TransactionMonthStatusSuccess> fillSuccessMonths(List<TransactionMonthStatusSuccess> rows, int year) {
        String reportYear = String.valueOf(year);
        Map<String, TransactionMonthStatusSuccess> byMonth = rows.stream()
                .collect(Collectors.toMap(TransactionMonthStatusSuccess::getMonthName, Function.identity(),
                        (a, b) -> a));
        List<TransactionMonthStatusSuccess> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month,
                    new TransactionMonthStatusSuccess(reportYear, month, 0L, 0L)));
        }
        return result;
    }

    private List<TransactionMonthStatusFailed> fillFailedMonths(List<TransactionMonthStatusFailed> rows, int year) {
        String reportYear = String.valueOf(year);
        Map<String, TransactionMonthStatusFailed> byMonth = rows.stream()
                .collect(Collectors.toMap(TransactionMonthStatusFailed::getMonthName, Function.identity(),
                        (a, b) -> a));
        List<TransactionMonthStatusFailed> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month,
                    new TransactionMonthStatusFailed(reportYear, month, 0L, 0L)));
        }
        return result;
    }

    private List<TransactionYearStatusSuccess> fillSuccessYears(List<TransactionYearStatusSuccess> rows, long year, int count,
            boolean descending) {
        Map<String, TransactionYearStatusSuccess> byYear = rows.stream()
                .collect(Collectors.toMap(TransactionYearStatusSuccess::getReportYear, Function.identity(),
                        (a, b) -> a));
        List<TransactionYearStatusSuccess> result = new ArrayList<>(count);
        if (descending) {
            for (long y = year; y >= year - (count - 1); y--) {
                result.add(byYear.getOrDefault(String.valueOf(y),
                        new TransactionYearStatusSuccess(String.valueOf(y), 0L, 0L)));
            }
        } else {
            for (long y = year - (count - 1); y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y),
                        new TransactionYearStatusSuccess(String.valueOf(y), 0L, 0L)));
            }
        }
        return result;
    }

    private List<TransactionYearStatusFailed> fillFailedYears(List<TransactionYearStatusFailed> rows, long year, int count,
            boolean descending) {
        Map<String, TransactionYearStatusFailed> byYear = rows.stream()
                .collect(Collectors.toMap(TransactionYearStatusFailed::getReportYear, Function.identity(),
                        (a, b) -> a));
        List<TransactionYearStatusFailed> result = new ArrayList<>(count);
        if (descending) {
            for (long y = year; y >= year - (count - 1); y--) {
                result.add(byYear.getOrDefault(String.valueOf(y),
                        new TransactionYearStatusFailed(String.valueOf(y), 0L, 0L)));
            }
        } else {
            for (long y = year - (count - 1); y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y),
                        new TransactionYearStatusFailed(String.valueOf(y), 0L, 0L)));
            }
        }
        return result;
    }
}
