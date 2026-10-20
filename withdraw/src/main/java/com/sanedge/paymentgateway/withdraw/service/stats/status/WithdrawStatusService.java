package com.sanedge.paymentgateway.withdraw.service.stats.status;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.withdraw.domain.requests.MonthStatusWithdraw;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess;

public interface WithdrawStatusService {
    ApiResponse<List<WithdrawResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusWithdraw req);

    ApiResponse<List<WithdrawResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<WithdrawResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusWithdraw req);

    ApiResponse<List<WithdrawResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
