package com.sanedge.paymentgateway.transaction.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthAmountResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearlyAmountResponse;

public interface TransactionAmountService {
    ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(Long year);

    ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(Long year);
}
