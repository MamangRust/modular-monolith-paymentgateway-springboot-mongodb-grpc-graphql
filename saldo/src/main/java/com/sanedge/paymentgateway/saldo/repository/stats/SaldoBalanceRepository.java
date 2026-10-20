package com.sanedge.paymentgateway.saldo.repository.stats;

import com.sanedge.paymentgateway.saldo.models.Saldo;
import com.sanedge.paymentgateway.saldo.models.SaldoMonthBalance;
import com.sanedge.paymentgateway.saldo.models.SaldoYearBalance;

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
public interface SaldoBalanceRepository extends MongoRepository<Saldo, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, created_at: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $month: '$created_at' }, totalBalance: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalBalance: { $ifNull: ['$totalBalance', 0] } } }"
    })
    List<SaldoMonthBalance> aggregateFindMonthlySaldoBalances(LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<SaldoMonthBalance> findMonthlySaldoBalances(Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlySaldoBalances(start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, created_at: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $year: '$created_at' }, totalBalance: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalBalance: { $ifNull: ['$totalBalance', 0] } } }"
    })
    List<SaldoYearBalance> aggregateFindYearlySaldoBalances(LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<SaldoYearBalance> findYearlySaldoBalances(Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlySaldoBalances(start, start.plusYears(5)), year, false);
    }

    private List<SaldoMonthBalance> fillMonths(List<SaldoMonthBalance> rows) {
        Map<String, SaldoMonthBalance> byMonth = rows.stream()
                .collect(Collectors.toMap(SaldoMonthBalance::getMonthName, Function.identity(), (a, b) -> a));
        List<SaldoMonthBalance> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new SaldoMonthBalance(month, 0L)));
        }
        return result;
    }

    private List<SaldoYearBalance> fillYears(List<SaldoYearBalance> rows, long year, boolean descending) {
        Map<String, SaldoYearBalance> byYear = rows.stream()
                .collect(Collectors.toMap(SaldoYearBalance::getReportYear, Function.identity(), (a, b) -> a));
        List<SaldoYearBalance> result = new ArrayList<>(5);
        if (descending) {
            for (long y = year; y >= year - 4; y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new SaldoYearBalance(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - 4; y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new SaldoYearBalance(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
