package com.sanedge.paymentgateway.withdraw.service.stats.status;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.MonthStatusWithdrawCardNumber;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.YearStatusWithdrawCardNumber;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess;

public interface WithdrawStatusByCardService {
    ApiResponse<List<WithdrawResponseMonthStatusSuccess>> findMonthStatusSuccessByCard(
            MonthStatusWithdrawCardNumber req);

    ApiResponse<List<WithdrawResponseYearStatusSuccess>> findYearlyStatusSuccessByCard(
            YearStatusWithdrawCardNumber req);

    ApiResponse<List<WithdrawResponseMonthStatusFailed>> findMonthStatusFailedByCard(MonthStatusWithdrawCardNumber req);

    ApiResponse<List<WithdrawResponseYearStatusFailed>> findYearlyStatusFailedByCard(YearStatusWithdrawCardNumber req);
}
