package com.sanedge.paymentgateway.transaction.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthStatusTransactionCardNumber;
import com.sanedge.paymentgateway.transaction.domain.requests.YearStatusTransactionCardNumber;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess;

public interface TransactionStatusByCardService {
    ApiResponse<List<TransactionResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusTransactionCardNumber req);

    ApiResponse<List<TransactionResponseYearStatusSuccess>> findYearlyStatusSuccess(YearStatusTransactionCardNumber req);

    ApiResponse<List<TransactionResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransactionCardNumber req);

    ApiResponse<List<TransactionResponseYearStatusFailed>> findYearlyStatusFailed(YearStatusTransactionCardNumber req);
}
