package com.sanedge.paymentgateway.transaction.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthYearPaymentMethod;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthAmountResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearlyAmountResponse;

public interface TransactionAmountByCardService {
    ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(MonthYearPaymentMethod req);

    ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(MonthYearPaymentMethod req);
}
