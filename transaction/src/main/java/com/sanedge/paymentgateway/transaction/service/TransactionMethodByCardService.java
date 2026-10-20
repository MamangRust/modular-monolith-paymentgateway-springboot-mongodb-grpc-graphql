package com.sanedge.paymentgateway.transaction.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthYearPaymentMethod;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse;
import com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse;

public interface TransactionMethodByCardService {
    ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(MonthYearPaymentMethod req);

    ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(MonthYearPaymentMethod req);
}
