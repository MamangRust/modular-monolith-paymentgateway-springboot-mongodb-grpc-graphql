package com.sanedge.paymentgateway.transfer.service;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthStatusTransferCardNumber;
import com.sanedge.paymentgateway.transfer.domain.requests.YearStatusTransferCardNumber;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed;
import com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess;

public interface TransferStatusByCardService {
    ApiResponse<List<TransferResponseMonthStatusSuccess>> findMonthStatusSuccessByCard(
            MonthStatusTransferCardNumber req);

    ApiResponse<List<TransferResponseYearStatusSuccess>> findYearlyStatusSuccessByCard(
            YearStatusTransferCardNumber req);

    ApiResponse<List<TransferResponseMonthStatusFailed>> findMonthStatusFailedByCard(MonthStatusTransferCardNumber req);

    ApiResponse<List<TransferResponseYearStatusFailed>> findYearlyStatusFailedByCard(YearStatusTransferCardNumber req);
}
