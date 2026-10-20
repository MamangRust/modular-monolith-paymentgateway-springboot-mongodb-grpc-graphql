package com.sanedge.paymentgateway.card.repository.statsbycard;

import com.sanedge.paymentgateway.card.models.Card;
import com.sanedge.paymentgateway.card.models.CardMonthBalance;
import com.sanedge.paymentgateway.card.models.CardYearBalance;

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
public interface CardBalanceByCardRepository extends MongoRepository<Card, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, created_at: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$created_at' }, totalBalance: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalBalance: { $ifNull: ['$totalBalance', 0] } } }"
    })
    List<CardMonthBalance> aggregateGetMonthlyBalancesByCard(String cardNumber, LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardMonthBalance> getMonthlyBalancesByCard(LocalDate yearDate, String cardNumber) {
        LocalDateTime start = LocalDate.of(yearDate.getYear(), 1, 1).atStartOfDay();
        return fillMonths(aggregateGetMonthlyBalancesByCard(cardNumber, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, created_at: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$created_at' }, totalBalance: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalBalance: { $ifNull: ['$totalBalance', 0] } } }"
    })
    List<CardYearBalance> aggregateGetYearlyBalancesByCard(String cardNumber, LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardYearBalance> getYearlyBalancesByCard(Long year, String cardNumber) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateGetYearlyBalancesByCard(cardNumber, start, start.plusYears(5)), year, false);
    }

    private List<CardMonthBalance> fillMonths(List<CardMonthBalance> rows) {
        Map<String, CardMonthBalance> byMonth = rows.stream()
                .collect(Collectors.toMap(CardMonthBalance::getMonthName, Function.identity(), (a, b) -> a));
        List<CardMonthBalance> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new CardMonthBalance(month, 0L)));
        }
        return result;
    }

    private List<CardYearBalance> fillYears(List<CardYearBalance> rows, long year, boolean descending) {
        Map<String, CardYearBalance> byYear = rows.stream()
                .collect(Collectors.toMap(CardYearBalance::getReportYear, Function.identity(), (a, b) -> a));
        List<CardYearBalance> result = new ArrayList<>(5);
        if (descending) {
            for (long y = year; y >= year - 4; y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new CardYearBalance(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - 4; y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new CardYearBalance(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
