package com.sanedge.paymentgateway.topup.service.stats.status;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.topup.domain.requests.stats.MonthTopupStatus;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusSuccess;

public interface TopupStatusService {
    ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthTopupStatus req);

    ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatus req);

    ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
