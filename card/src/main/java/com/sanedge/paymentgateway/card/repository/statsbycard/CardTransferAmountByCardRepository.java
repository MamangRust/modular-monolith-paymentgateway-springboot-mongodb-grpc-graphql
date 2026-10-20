package com.sanedge.paymentgateway.card.repository.statsbycard;

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
public interface CardTransferAmountByCardRepository extends MongoRepository<Card, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_from: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<CardMonthAmount> aggregateGetMonthlyTransferAmountBySender(String sender, LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardMonthAmount> getMonthlyTransferAmountBySender(String sender, LocalDate yearDate) {
        LocalDateTime start = LocalDate.of(yearDate.getYear(), 1, 1).atStartOfDay();
        return fillMonths(aggregateGetMonthlyTransferAmountBySender(sender, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_from: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<CardYearAmount> aggregateGetYearlyTransferAmountBySender(String sender, LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardYearAmount> getYearlyTransferAmountBySender(String sender, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateGetYearlyTransferAmountBySender(sender, start, start.plusYears(5)), year, false);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_to: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<CardMonthAmount> aggregateGetMonthlyTransferAmountByReceiver(String receiver, LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardMonthAmount> getMonthlyTransferAmountByReceiver(String receiver, LocalDate yearDate) {
        LocalDateTime start = LocalDate.of(yearDate.getYear(), 1, 1).atStartOfDay();
        return fillMonths(aggregateGetMonthlyTransferAmountByReceiver(receiver, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_to: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<CardYearAmount> aggregateGetYearlyTransferAmountByReceiver(String receiver, LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<CardYearAmount> getYearlyTransferAmountByReceiver(String receiver, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateGetYearlyTransferAmountByReceiver(receiver, start, start.plusYears(5)), year, false);
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
