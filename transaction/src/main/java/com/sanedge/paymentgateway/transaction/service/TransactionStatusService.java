package com.sanedge.paymentgateway.transaction.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthStatusTransaction;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess;

public interface TransactionStatusService {
    ApiResponse<List<TransactionResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusTransaction req);

    ApiResponse<List<TransactionResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<TransactionResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransaction req);

    ApiResponse<List<TransactionResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
