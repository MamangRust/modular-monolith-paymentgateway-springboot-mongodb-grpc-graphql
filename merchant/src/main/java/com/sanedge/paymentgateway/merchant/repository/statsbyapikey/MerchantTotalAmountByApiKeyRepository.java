package com.sanedge.paymentgateway.merchant.repository.statsbyapikey;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyTotalAmount;
import com.sanedge.paymentgateway.merchant.models.Transaction;

/**
 * Total-amount stats for a merchant addressed by API key. The SQL inner-joined
 * {@code transactions} with {@code merchants} filtered by {@code api_key} and
 * produced the current/previous month (or requested/previous year), filling
 * missing rows with 0.
 */
@Repository
public interface MerchantTotalAmountByApiKeyRepository extends MongoRepository<Transaction, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    String LOOKUP = "{ $lookup: { from: 'merchants', localField: 'merchant_id', foreignField: 'merchant_id', as: '_merchant' } }";

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null } }",
            LOOKUP,
            "{ $unwind: '$_merchant' }",
            "{ $match: { '_merchant.deleted_at': null, '_merchant.api_key': ?0, 'transaction_time': { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { year: { $year: '$transaction_time' }, month: { $month: '$transaction_time' } }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id.year' }, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id.month', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<MerchantMonthlyTotalAmount> aggregateFindMonthlyTotalAmountByApiKey(String apiKey, LocalDate startInclusive,
            LocalDate endExclusive);

    default List<MerchantMonthlyTotalAmount> findMonthlyTotalAmountByApiKey(String apiKey, LocalDate date) {
        // SQL window: [date - 1 month, date + 1 month), newest first.
        LocalDate start = date.minusMonths(1).withDayOfMonth(1);
        LocalDate end = date.plusMonths(1).withDayOfMonth(1);
        List<MerchantMonthlyTotalAmount> rows = aggregateFindMonthlyTotalAmountByApiKey(apiKey, start, end);
        Map<Integer, MerchantMonthlyTotalAmount> bySort = rows.stream().collect(Collectors.toMap(
                r -> Integer.parseInt(r.getReportYear()) * 100 + monthIndex(r.getMonthName()),
                Function.identity(), (a, b) -> a));
        List<MerchantMonthlyTotalAmount> result = new ArrayList<>(2);
        for (LocalDate d : List.of(date, date.minusMonths(1))) {
            int key = d.getYear() * 100 + d.getMonthValue();
            MerchantMonthlyTotalAmount row = bySort.get(key);
            result.add(row != null ? row
                    : new MerchantMonthlyTotalAmount(String.valueOf(d.getYear()), MONTHS[d.getMonthValue() - 1], 0L));
        }
        return result;
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null } }",
            LOOKUP,
            "{ $unwind: '$_merchant' }",
            "{ $match: { '_merchant.deleted_at': null, '_merchant.api_key': ?0, $expr: { $in: [ { $year: '$transaction_time' }, [ ?1, ?2 ] ] } } }",
            "{ $group: { _id: { $year: '$transaction_time' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<MerchantYearlyTotalAmount> aggregateFindYearlyTotalAmountByApiKey(String apiKey, long year, long previousYear);

    default List<MerchantYearlyTotalAmount> findYearlyTotalAmountByApiKey(String apiKey, Long year,
            Long previousYear) {
        List<MerchantYearlyTotalAmount> rows = aggregateFindYearlyTotalAmountByApiKey(apiKey, year, previousYear);
        Map<String, MerchantYearlyTotalAmount> byYear = rows.stream()
                .collect(Collectors.toMap(MerchantYearlyTotalAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<MerchantYearlyTotalAmount> result = new ArrayList<>(2);
        for (long y : List.of(year, previousYear)) {
            result.add(byYear.getOrDefault(String.valueOf(y), new MerchantYearlyTotalAmount(String.valueOf(y), 0L)));
        }
        return result;
    }

    private int monthIndex(String monthName) {
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equals(monthName)) {
                return i + 1;
            }
        }
        return 0;
    }
}
