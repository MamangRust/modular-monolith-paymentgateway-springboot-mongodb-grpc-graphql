package com.sanedge.paymentgateway.topup.repository.statsbycard;

import com.sanedge.paymentgateway.topup.models.Topup;
import com.sanedge.paymentgateway.topup.models.TopupMonthAmount;
import com.sanedge.paymentgateway.topup.models.TopupYearAmount;

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
public interface TopupAmountByCardRepository extends MongoRepository<Topup, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, topup_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$topup_time' }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupMonthAmount> aggregateFindMonthlyTopupAmountsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TopupMonthAmount> findMonthlyTopupAmountsByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyTopupAmountsByCard(cardNumber, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, topup_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$topup_time' }, totalAmount: { $sum: '$topup_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TopupYearAmount> aggregateFindYearlyTopupAmountsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<TopupYearAmount> findYearlyTopupAmountsByCard(String cardNumber, Long endYear) {
        LocalDateTime start = LocalDate.of(endYear.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyTopupAmountsByCard(cardNumber, start, start.plusYears(5)), endYear, false);
    }

    private List<TopupMonthAmount> fillMonths(List<TopupMonthAmount> rows) {
        Map<String, TopupMonthAmount> byMonth = rows.stream()
                .collect(Collectors.toMap(TopupMonthAmount::getMonthName, Function.identity(), (a, b) -> a));
        List<TopupMonthAmount> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new TopupMonthAmount(month, 0L)));
        }
        return result;
    }

    private List<TopupYearAmount> fillYears(List<TopupYearAmount> rows, long year, boolean descending) {
        Map<String, TopupYearAmount> byYear = rows.stream()
                .collect(Collectors.toMap(TopupYearAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<TopupYearAmount> result = new ArrayList<>(5);
        if (descending) {
            for (long y = year; y >= year - 4; y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new TopupYearAmount(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - 4; y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new TopupYearAmount(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
