package com.sanedge.paymentgateway.transfer.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthYearCardNumber;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse;

public interface TransferAmountByCardService {
    ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmountsBySender(MonthYearCardNumber req);

    ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmountsByReceiver(MonthYearCardNumber req);

    ApiResponse<List<TransferYearAmountResponse>> findYearlyAmountsBySender(MonthYearCardNumber req);

    ApiResponse<List<TransferYearAmountResponse>> findYearlyAmountsByReceiver(MonthYearCardNumber req);
}
