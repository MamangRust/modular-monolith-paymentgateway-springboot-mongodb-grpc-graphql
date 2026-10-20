package com.sanedge.paymentgateway.transaction.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse;

public interface TransactionMethodService {
    ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(Long year);

    ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(Long year);
}
