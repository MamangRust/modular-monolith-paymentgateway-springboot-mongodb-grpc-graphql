package com.sanedge.paymentgateway.merchant.repository.statsbyid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.models.Transaction;

/**
 * Monthly/yearly totals grouped by payment method for a single merchant. The SQL
 * CROSS JOINed the 12 months (or 5 years) with the merchant's distinct payment
 * methods; the default methods rebuild the full month × method grid.
 */
@Repository
public interface MerchantMethodByIdRepository extends MongoRepository<Transaction, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, merchant_id: ?0, transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { month: { $month: '$transaction_time' }, method: '$payment_method' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id.month', 1] }] }, paymentMethod: '$_id.method', totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<MerchantMonthlyPaymentMethod> aggregateFindMonthlyPaymentMethodsById(String merchantId,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, merchant_id: ?0 } }",
            "{ $group: { _id: '$payment_method' } }",
            "{ $project: { _id: 0, paymentMethod: '$_id' } }"
    })
    List<MerchantMonthlyPaymentMethod> aggregateDistinctPaymentMethodsById(String merchantId);

    default List<MerchantMonthlyPaymentMethod> findMonthlyPaymentMethodsById(String merchantId, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyPaymentMethodsById(merchantId, start, start.plusYears(1)),
                distinctPaymentMethods(merchantId));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, merchant_id: ?0, transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { year: { $year: '$transaction_time' }, method: '$payment_method' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id.year' }, paymentMethod: '$_id.method', totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<MerchantYearlyPaymentMethod> aggregateFindYearlyPaymentMethodsById(String merchantId,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<MerchantYearlyPaymentMethod> findYearlyPaymentMethodsById(String merchantId, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyPaymentMethodsById(merchantId, start, start.plusYears(5)), year,
                distinctPaymentMethods(merchantId));
    }

    private List<String> distinctPaymentMethods(String merchantId) {
        Set<String> methods = new LinkedHashSet<>();
        for (MerchantMonthlyPaymentMethod row : aggregateDistinctPaymentMethodsById(merchantId)) {
            if (row.getPaymentMethod() != null) {
                methods.add(row.getPaymentMethod());
            }
        }
        List<String> sorted = new ArrayList<>(methods);
        sorted.sort(String::compareTo);
        return sorted;
    }

    private List<MerchantMonthlyPaymentMethod> fillMonths(List<MerchantMonthlyPaymentMethod> rows,
            List<String> methods) {
        Map<String, MerchantMonthlyPaymentMethod> byKey = new LinkedHashMap<>();
        for (MerchantMonthlyPaymentMethod row : rows) {
            byKey.put(row.getMonthName() + "|" + row.getPaymentMethod(), row);
        }
        List<MerchantMonthlyPaymentMethod> result = new ArrayList<>(MONTHS.length * Math.max(1, methods.size()));
        for (String month : MONTHS) {
            for (String method : methods) {
                MerchantMonthlyPaymentMethod row = byKey.get(month + "|" + method);
                result.add(row != null ? row : new MerchantMonthlyPaymentMethod(month, method, 0L));
            }
        }
        return result;
    }

    private List<MerchantYearlyPaymentMethod> fillYears(List<MerchantYearlyPaymentMethod> rows, long year,
            List<String> methods) {
        Map<String, MerchantYearlyPaymentMethod> byKey = new LinkedHashMap<>();
        for (MerchantYearlyPaymentMethod row : rows) {
            byKey.put(row.getReportYear() + "|" + row.getPaymentMethod(), row);
        }
        List<MerchantYearlyPaymentMethod> result = new ArrayList<>(5 * Math.max(1, methods.size()));
        for (long y = year - 4; y <= year; y++) {
            for (String method : methods) {
                MerchantYearlyPaymentMethod row = byKey.get(y + "|" + method);
                result.add(row != null ? row : new MerchantYearlyPaymentMethod(String.valueOf(y), method, 0L));
            }
        }
        return result;
    }
}
