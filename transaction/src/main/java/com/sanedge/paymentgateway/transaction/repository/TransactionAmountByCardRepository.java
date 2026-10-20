package com.sanedge.paymentgateway.transaction.repository;

import com.sanedge.paymentgateway.transaction.models.Transaction;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthAmount;
import com.sanedge.paymentgateway.transaction.models.TransactionYearAmount;

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
public interface TransactionAmountByCardRepository extends MongoRepository<Transaction, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transaction_time' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionMonthAmount> aggregateFindMonthlyAmountsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransactionMonthAmount> findMonthlyAmountsByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyAmountsByCard(cardNumber, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transaction_time' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionYearAmount> aggregateFindYearlyAmountsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransactionYearAmount> findYearlyAmountsByCard(String cardNumber, Long endYear) {
        LocalDateTime start = LocalDate.of(endYear.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyAmountsByCard(cardNumber, start, start.plusYears(5)), endYear, 5, false);
    }

    private List<TransactionMonthAmount> fillMonths(List<TransactionMonthAmount> rows) {
        Map<String, TransactionMonthAmount> byMonth = rows.stream()
                .collect(Collectors.toMap(TransactionMonthAmount::getMonthName, Function.identity(), (a, b) -> a));
        List<TransactionMonthAmount> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new TransactionMonthAmount(month, 0L)));
        }
        return result;
    }

    private List<TransactionYearAmount> fillYears(List<TransactionYearAmount> rows, long year, int count,
            boolean descending) {
        Map<String, TransactionYearAmount> byYear = rows.stream()
                .collect(Collectors.toMap(TransactionYearAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<TransactionYearAmount> result = new ArrayList<>(count);
        if (descending) {
            for (long y = year; y >= year - (count - 1); y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new TransactionYearAmount(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - (count - 1); y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new TransactionYearAmount(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
