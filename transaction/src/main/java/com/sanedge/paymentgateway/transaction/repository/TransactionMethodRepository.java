package com.sanedge.paymentgateway.transaction.repository;

import com.sanedge.paymentgateway.transaction.models.Transaction;
import com.sanedge.paymentgateway.transaction.models.TransactionMonthMethod;
import com.sanedge.paymentgateway.transaction.models.TransactionYearMethod;

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
public interface TransactionMethodRepository extends MongoRepository<Transaction, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transaction_time: { $gte: ?0, $lt: ?1 }, payment_method: { $ne: null } } }",
            "{ $group: { _id: { month: { $month: '$transaction_time' }, paymentMethod: '$payment_method' }, totalTransactions: { $sum: 1 }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id.month', 1] }] }, paymentMethod: '$_id.paymentMethod', totalTransactions: { $ifNull: ['$totalTransactions', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionMonthMethod> aggregateFindMonthlyPaymentMethods(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransactionMonthMethod> findMonthlyPaymentMethods(Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonthsByMethod(aggregateFindMonthlyPaymentMethods(start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transaction_time: { $gte: ?0, $lt: ?1 }, payment_method: { $ne: null } } }",
            "{ $group: { _id: { year: { $year: '$transaction_time' }, paymentMethod: '$payment_method' }, totalTransactions: { $sum: 1 }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id.year' }, paymentMethod: '$_id.paymentMethod', totalTransactions: { $ifNull: ['$totalTransactions', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransactionYearMethod> aggregateFindYearlyPaymentMethods(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TransactionYearMethod> findYearlyPaymentMethods(Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYearsByMethod(aggregateFindYearlyPaymentMethods(start, start.plusYears(5)), year, 5, false);
    }

    private List<TransactionMonthMethod> fillMonthsByMethod(List<TransactionMonthMethod> rows) {
        List<String> methods = rows.stream()
                .map(TransactionMonthMethod::getPaymentMethod)
                .distinct()
                .sorted()
                .toList();
        Map<String, TransactionMonthMethod> byKey = rows.stream()
                .collect(Collectors.toMap(r -> r.getMonthName() + '|' + r.getPaymentMethod(), Function.identity(),
                        (a, b) -> a));
        List<TransactionMonthMethod> result = new ArrayList<>(MONTHS.length * methods.size());
        for (String month : MONTHS) {
            for (String method : methods) {
                result.add(byKey.getOrDefault(month + '|' + method,
                        new TransactionMonthMethod(month, method, 0L, 0L)));
            }
        }
        return result;
    }

    private List<TransactionYearMethod> fillYearsByMethod(List<TransactionYearMethod> rows, long year, int count,
            boolean descending) {
        List<String> methods = rows.stream()
                .map(TransactionYearMethod::getPaymentMethod)
                .distinct()
                .sorted()
                .toList();
        Map<String, TransactionYearMethod> byKey = rows.stream()
                .collect(Collectors.toMap(r -> r.getReportYear() + '|' + r.getPaymentMethod(), Function.identity(),
                        (a, b) -> a));
        List<Long> years = new ArrayList<>(count);
        if (descending) {
            for (long y = year; y >= year - (count - 1); y--) {
                years.add(y);
            }
        } else {
            for (long y = year - (count - 1); y <= year; y++) {
                years.add(y);
            }
        }
        List<TransactionYearMethod> result = new ArrayList<>(years.size() * methods.size());
        for (Long y : years) {
            for (String method : methods) {
                result.add(byKey.getOrDefault(y + "|" + method,
                        new TransactionYearMethod(String.valueOf(y), method, 0L, 0L)));
            }
        }
        return result;
    }
}
