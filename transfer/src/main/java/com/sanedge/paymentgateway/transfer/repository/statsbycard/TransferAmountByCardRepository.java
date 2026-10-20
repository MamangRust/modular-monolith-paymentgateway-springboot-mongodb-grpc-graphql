package com.sanedge.paymentgateway.transfer.repository.statsbycard;

import com.sanedge.paymentgateway.transfer.models.Transfer;
import com.sanedge.paymentgateway.transfer.models.TransferMonthAmount;
import com.sanedge.paymentgateway.transfer.models.TransferYearAmount;

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
public interface TransferAmountByCardRepository extends MongoRepository<Transfer, String> {

    String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_from: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferMonthAmount> aggregateFindMonthlyTransferAmountsBySender(String transferFrom,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferMonthAmount> findMonthlyTransferAmountsBySender(String transferFrom, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyTransferAmountsBySender(transferFrom, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_from: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferYearAmount> aggregateFindYearlyTransferAmountsBySender(String transferFrom,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferYearAmount> findYearlyTransferAmountsBySender(String transferFrom, Long endYear) {
        LocalDateTime start = LocalDate.of(endYear.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyTransferAmountsBySender(transferFrom, start, start.plusYears(5)), endYear,
                false);
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_to: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $month: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, monthName: { $arrayElemAt: [ [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ], { $subtract: ['$_id', 1] }] }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferMonthAmount> aggregateFindMonthlyTransferAmountsByReceiver(String transferTo,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferMonthAmount> findMonthlyTransferAmountsByReceiver(String transferTo, Long year) {
        LocalDateTime start = LocalDate.of(year.intValue(), 1, 1).atStartOfDay();
        return fillMonths(aggregateFindMonthlyTransferAmountsByReceiver(transferTo, start, start.plusYears(1)));
    }

    @Aggregation(pipeline = {
            "{ $match: { deleted_at: null, transfer_to: ?0, transfer_time: { $gte: ?1, $lt: ?2 } } }",
            "{ $group: { _id: { $year: '$transfer_time' }, totalAmount: { $sum: '$transfer_amount' } } }",
            "{ $project: { _id: 0, reportYear: { $toString: '$_id' }, totalAmount: { $ifNull: ['$totalAmount', 0] } } }"
    })
    List<TransferYearAmount> aggregateFindYearlyTransferAmountsByReceiver(String transferTo,
            LocalDateTime startInclusive, LocalDateTime endExclusive);

    default List<TransferYearAmount> findYearlyTransferAmountsByReceiver(String transferTo, Long endYear) {
        LocalDateTime start = LocalDate.of(endYear.intValue() - 4, 1, 1).atStartOfDay();
        return fillYears(aggregateFindYearlyTransferAmountsByReceiver(transferTo, start, start.plusYears(5)), endYear,
                false);
    }

    private List<TransferMonthAmount> fillMonths(List<TransferMonthAmount> rows) {
        Map<String, TransferMonthAmount> byMonth = rows.stream()
                .collect(Collectors.toMap(TransferMonthAmount::getMonthName, Function.identity(), (a, b) -> a));
        List<TransferMonthAmount> result = new ArrayList<>(MONTHS.length);
        for (String month : MONTHS) {
            result.add(byMonth.getOrDefault(month, new TransferMonthAmount(month, 0L)));
        }
        return result;
    }

    private List<TransferYearAmount> fillYears(List<TransferYearAmount> rows, long year, boolean descending) {
        Map<String, TransferYearAmount> byYear = rows.stream()
                .collect(Collectors.toMap(TransferYearAmount::getReportYear, Function.identity(), (a, b) -> a));
        List<TransferYearAmount> result = new ArrayList<>(5);
        if (descending) {
            for (long y = year; y >= year - 4; y--) {
                result.add(byYear.getOrDefault(String.valueOf(y), new TransferYearAmount(String.valueOf(y), 0L)));
            }
        } else {
            for (long y = year - 4; y <= year; y++) {
                result.add(byYear.getOrDefault(String.valueOf(y), new TransferYearAmount(String.valueOf(y), 0L)));
            }
        }
        return result;
    }
}
