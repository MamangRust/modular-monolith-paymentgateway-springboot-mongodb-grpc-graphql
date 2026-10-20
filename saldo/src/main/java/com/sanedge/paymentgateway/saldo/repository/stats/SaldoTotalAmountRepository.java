package com.sanedge.paymentgateway.saldo.repository.stats;

import com.sanedge.paymentgateway.saldo.models.Saldo;
import com.sanedge.paymentgateway.saldo.models.SaldoMonthTotalBalance;
import com.sanedge.paymentgateway.saldo.models.SaldoYearTotalBalance;

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
 * The SQL version of {@link #findMonthlyTotalSaldoBalance} only used its
 * {@code startYear} parameter (startMonth/endYear/endMonth were accepted but
 * ignored), aggregated by month of {@code created_at} for that whole year and
 * ordered the 12 calendar months DESCENDING (Dec..Jan); the yearly query
 * returned the two years {@code year} and {@code year - 1} in descending
 * order. Those semantics are preserved here.
 */
@Repository
public interface SaldoTotalAmountRepository extends MongoRepository<Saldo, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, created_at: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $month: '$created_at' }, totalBalance: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalBalance: { $ifNull: ['$totalBalance', 0] } } }"
    })
    List<SaldoMonthTotalBalance> aggregateFindMonthlyTotalSaldoBalance(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<SaldoMonthTotalBalance> findMonthlyTotalSaldoBalance(Long startYear, Integer startMonth,
            Long endYear, Integer endMonth) {
        LocalDateTime start = LocalDate.of(startYear.intValue(), 1, 1).atStartOfDay();
        return fillMonthsDescending(aggregateFindMonthlyTotalSaldoBalance(start, start.plusYears(1)), startYear);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, created_at: { $gte: ?0, $lt: ?1 } } }",
            "{ $group: { _id: { $year: '$created_at' }, totalBalance: { $sum: '$total_balance' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalBalance: { $ifNull: ['$totalBalance', 0] } } }"
    })
    List<SaldoYearTotalBalance> aggregateFindYearlyTotalSaldoBalance(LocalDateTime startInclusive,
            LocalDateTime endExclusive);

    default List<SaldoYearTotalBalance> findYearlyTotalSaldoBalance(Long year) {
        LocalDateTime start = LocalDate.of(year.intValue() - 1, 1, 1).atStartOfDay();
        return fillYearsDescending(aggregateFindYearlyTotalSaldoBalance(start, start.plusYears(2)), year);
    }

    private List<SaldoMonthTotalBalance> fillMonthsDescending(List<SaldoMonthTotalBalance> rows, Long startYear) {
        Map<String, SaldoMonthTotalBalance> byMonth = rows.stream()
                .collect(Collectors.toMap(SaldoMonthTotalBalance::getMonthName, Function.identity(), (a, b) -> a));
        List<SaldoMonthTotalBalance> result = new ArrayList<>(MONTHS.length);
        for (int i = MONTHS.length - 1; i >= 0; i--) {
            String month = MONTHS[i];
            result.add(byMonth.getOrDefault(month, new SaldoMonthTotalBalance(String.valueOf(startYear), month, 0L)));
        }
        return result;
    }

    private List<SaldoYearTotalBalance> fillYearsDescending(List<SaldoYearTotalBalance> rows, long year) {
        Map<String, SaldoYearTotalBalance> byYear = rows.stream()
                .collect(Collectors.toMap(SaldoYearTotalBalance::getReportYear, Function.identity(), (a, b) -> a));
        List<SaldoYearTotalBalance> result = new ArrayList<>(2);
        for (long y = year; y >= year - 1; y--) {
            result.add(byYear.getOrDefault(String.valueOf(y), new SaldoYearTotalBalance(String.valueOf(y), 0L)));
        }
        return result;
    }
}
