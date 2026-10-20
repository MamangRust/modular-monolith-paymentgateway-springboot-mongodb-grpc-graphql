package com.sanedge.paymentgateway.card.repository.stats;

import com.sanedge.paymentgateway.card.models.Card;
import com.sanedge.paymentgateway.card.models.CardMonthAmount;
import com.sanedge.paymentgateway.card.models.CardYearAmount;

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
public interface CardTopupAmountRepository extends MongoRepository<Card, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, topup_time: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $month: '$topup_time' }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<CardMonthAmount> aggregateGetMonthlyTopupAmount(LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardMonthAmount> getMonthlyTopupAmount(LocalDate yearDate) {
        LocalDateTime start = LocalDate.of(yearDate.getYear(), 1, 1).atStartOfDay();
        return fillMonths(aggregateGetMonthlyTopupAmount(start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, topup_time: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $year: '$topup_time' }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<CardYearAmount> aggregateGetYearlyTopupAmount(LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardYearAmount> getYearlyTopupAmount(Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateGetYearlyTopupAmount(start, start.plusYears(5)), year, false);
    }

    private List<CardMonthAmount> fillMonths(List<CardMonthAmount> rows) {
        Map<String, CardMonthAmount> byMonth = rows.stream()
                .collect(Collectors.toMap(CardMonthAmount::getMonthName, Function.identity(), (a, b) -> a));
        List<CardMonthAmount> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new CardMonthAmount(month, 0L)));
        }
        return result;
    }

    private List<CardYearAmount> fillYears(List<CardYearAmount> rows, long year, boolean descending) {
        Map<String, CardYearAmount> byYear = rows.stream()
                .collect(Collectors.toMap(CardYearAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<CardYearAmount> result = new ArrayList<>(5);
        if (descending) {
            for (long y = year; y >= year - 4; y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new CardYearAmount(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - 4; y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new CardYearAmount(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
