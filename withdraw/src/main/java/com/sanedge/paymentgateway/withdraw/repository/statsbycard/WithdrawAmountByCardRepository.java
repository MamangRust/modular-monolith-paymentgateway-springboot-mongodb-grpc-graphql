package com.sanedge.paymentgateway.withdraw.repository.statsbycard;

import com.sanedge.paymentgateway.withdraw.models.Withdraw;
import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthlyAmount;
import com.sanedge.paymentgateway.withdraw.models.WithdrawYearlyAmount;

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
public interface WithdrawAmountByCardRepository extends MongoRepository<Withdraw, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, withdraw_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$withdraw_time' }, totalAmount: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<WithdrawMonthlyAmount> aggregateFindMonthlyWithdrawsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<WithdrawMonthlyAmount> findMonthlyWithdrawsByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyWithdrawsByCard(cardNumber, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, card_number: ?0, withdraw_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$withdraw_time' }, totalAmount: { $sum: '$withdraw_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<WithdrawYearlyAmount> aggregateFindYearlyWithdrawsByCard(String cardNumber, LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<WithdrawYearlyAmount> findYearlyWithdrawsByCard(String cardNumber, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyWithdrawsByCard(cardNumber, start, start.plusYears(5)), year, false);
    }

    private List<WithdrawMonthlyAmount> fillMonths(List<WithdrawMonthlyAmount> rows) {
        Map<String, WithdrawMonthlyAmount> byMonth = rows.stream()
                .collect(Collectors.toMap(WithdrawMonthlyAmount::getMonthName, Function.identity(), (a, b) -> a));
        List<WithdrawMonthlyAmount> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new WithdrawMonthlyAmount(month, 0L)));
        }
        return result;
    }

    private List<WithdrawYearlyAmount> fillYears(List<WithdrawYearlyAmount> rows, long year, boolean descending) {
        Map<String, WithdrawYearlyAmount> byYear = rows.stream()
                .collect(Collectors.toMap(WithdrawYearlyAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<WithdrawYearlyAmount> result = new ArrayList<>(5);
        if (descending) {
            for (long y = year; y >= year - 4; y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new WithdrawYearlyAmount(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - 4; y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new WithdrawYearlyAmount(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
