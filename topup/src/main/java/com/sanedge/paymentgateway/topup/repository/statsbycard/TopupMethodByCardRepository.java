package com.sanedge.paymentgateway.topup.repository.statsbycard;

import com.sanedge.paymentgateway.topup.models.Topup;
import com.sanedge.paymentgateway.topup.models.TopupMonthMethod;
import com.sanedge.paymentgateway.topup.models.TopupYearMethod;

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
 * Per-card monthly/yearly topup-method stats. The SQL grouped topups of the
 * card by month/year and topup_method (group key is the field itself); the
 * fill helpers add the missing month x method combinations as 0 rows, like the
 * SQL's months CROSS JOIN topup_methods, ordered by period, then method.
 */
@Repository
public interface TopupMethodByCardRepository extends MongoRepository<Topup, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, topup_time: { $gte: ?1, $lt: ?2 }, topup_method: { $ne: null } } }",
            "{ $group: { _id: { month: { $month: '$topup_time' }, method: '$topup_method' }, totalTopups: { $sum: 1 }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id.month', 1] }] }, topupMethod: '$_id.method', totalTopups: { $ifNull: ['$totalTopups', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupMonthMethod> aggregateFindMonthlyTopupMethodsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TopupMonthMethod> findMonthlyTopupMethodsByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonthsByMethod(aggregateFindMonthlyTopupMethodsByCard(cardNumber, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, topup_time: { $gte: ?1, $lt: ?2 }, topup_method: { $ne: null } } }",
            "{ $group: { _id: { year: { $year: '$topup_time' }, method: '$topup_method' }, totalTopups: { $sum: 1 }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id.year' }, topupMethod: '$_id.method', totalTopups: { $ifNull: ['$totalTopups', 0] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupYearMethod> aggregateFindYearlyTopupMethodsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TopupYearMethod> findYearlyTopupMethodsByCardId(String cardNumber, Long endYear) {
        LocalDateTime start = LocalDate.of(endYear.intValue() - 4, 1, 1).atStartOfDay();
        return fillYearsByMethod(aggregateFindYearlyTopupMethodsByCard(cardNumber, start, start.plusYears(5)), endYear,
                5, false);
    }

    private List<TopupMonthMethod> fillMonthsByMethod(List<TopupMonthMethod> rows) {
        List<String> methods = rows.stream()
                .map(TopupMonthMethod::getTopupMethod)
                .distinct()
                .sorted()
                .toList();
        Map<String, TopupMonthMethod> byKey = rows.stream()
                .collect(Collectors.toMap(r -> r.getMonthName() + '|' + r.getTopupMethod(), Function.identity(),
                        (a, b) -> a));
        List<TopupMonthMethod> result = new ArrayList<>(MONTHS.length * methods.size());
        for (String month : MONTHS) {
            for (String method : methods) {
                result.add(byKey.getOrDefault(month + '|' + method,
                        new TopupMonthMethod(month, method, 0L, 0L)));
            }
        }
        return result;
    }

    private List<TopupYearMethod> fillYearsByMethod(List<TopupYearMethod> rows, long year, int count,
            boolean descending) {
        List<String> methods = rows.stream()
                .map(TopupYearMethod::getTopupMethod)
                .distinct()
                .sorted()
                .toList();
        Map<String, TopupYearMethod> byKey = rows.stream()
                .collect(Collectors.toMap(r -> r.getReportYear() + '|' + r.getTopupMethod(), Function.identity(),
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
        List<TopupYearMethod> result = new ArrayList<>(years.size() * methods.size());
        for (Long y : years) {
            for (String method : methods) {
                result.add(byKey.getOrDefault(y + "|" + method,
                        new TopupYearMethod(String.valueOf(y), method, 0L, 0L)));
            }
        }
        return result;
    }
}
