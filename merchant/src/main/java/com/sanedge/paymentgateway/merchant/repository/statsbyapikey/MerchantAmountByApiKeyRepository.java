package com.sanedge.paymentgateway.merchant.repository.statsbyapikey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyAmount;
import com.sanedge.paymentgateway.merchant.models.MerchantYearlyAmount;
import com.sanedge.paymentgateway.merchant.models.Transaction;

/**
 * Monthly/yearly transaction amount totals for a merchant addressed by API key.
 * The SQL joined {@code transactions} with {@code merchants} on
 * {@code api_key} (non-deleted merchants only); the {@code $lookup} preserves
 * the join and the api_key filter.
 */
@Repository
public interface MerchantAmountByApiKeyRepository extends MongoRepository<Transaction, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $lookup: { from: 'merchants', localField: 'merchant_id', foreignField: 'merchant_id', as: '_merchant' } }",
            "{ $unwind: '$_merchant' }",
            "{ $match: { '_merchant.deleted_at': null, '_merchant.api_key': ?0 } }",
            "{ $group: { _id: { $month: '$transaction_time' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<MerchantMonthlyAmount> aggregateFindMonthlyAmountByApiKey(String apiKey, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<MerchantMonthlyAmount> findMonthlyAmountByApiKey(String apiKey, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyAmountByApiKey(apiKey, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transaction_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $lookup: { from: 'merchants', localField: 'merchant_id', foreignField: 'merchant_id', as: '_merchant' } }",
            "{ $unwind: '$_merchant' }",
            "{ $match: { '_merchant.deleted_at': null, '_merchant.api_key': ?0 } }",
            "{ $group: { _id: { $year: '$transaction_time' }, totalAmount: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<MerchantYearlyAmount> aggregateFindYearlyAmountByApiKey(String apiKey, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<MerchantYearlyAmount> findYearlyAmountByApiKey(String apiKey, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyAmountByApiKey(apiKey, start, start.plusYears(5)), year);
    }

    private List<MerchantMonthlyAmount> fillMonths(List<MerchantMonthlyAmount> rows) {
        Map<String, MerchantMonthlyAmount> byMonth = rows.stream()
                .collect(Collectors.toMap(MerchantMonthlyAmount::getMonthName, Function.identity(), (a, b) -> a));
        List<MerchantMonthlyAmount> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new MerchantMonthlyAmount(month, 0L)));
        }
        return result;
    }

    private List<MerchantYearlyAmount> fillYears(List<MerchantYearlyAmount> rows, long year) {
        Map<String, MerchantYearlyAmount> byYear = rows.stream()
                .collect(Collectors.toMap(MerchantYearlyAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<MerchantYearlyAmount> result = new ArrayList<>(5);
        for (long y = year - 4; y <= year; y++) {
            result.add(byYear.getOrDefault(String.valueOf(y), new MerchantYearlyAmount(String.valueOf(y), 0L)));
        }
        return result;
    }
}
