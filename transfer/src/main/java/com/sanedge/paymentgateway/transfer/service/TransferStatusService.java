package com.sanedge.paymentgateway.transfer.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthStatusTransfer;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess;

public interface TransferStatusService {
    ApiResponse<List<TransferResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusTransfer req);

    ApiResponse<List<TransferResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<TransferResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransfer req);

    ApiResponse<List<TransferResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
