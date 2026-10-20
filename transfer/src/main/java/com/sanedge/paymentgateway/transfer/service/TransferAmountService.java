package com.sanedge.paymentgateway.transfer.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse;

public interface TransferAmountService {
    ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmounts(Long year);

    ApiResponse<List<TransferYearAmountResponse>> findYearlyAmounts(Long year);
}
