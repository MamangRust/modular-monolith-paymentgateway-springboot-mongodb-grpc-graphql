package com.sanedge.paymentgateway.topup.service.stats.status;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.topup.domain.requests.statsbycard.MonthTopupStatusCardNumber;
import com.sanedge.paymentgateway.topup.domain.requests.statsbycard.YearTopupStatusCardNumber;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusSuccess;

public interface TopupStatusByCardService {
    ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthTopupStatusCardNumber req);

    ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(YearTopupStatusCardNumber req);

    ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatusCardNumber req);

    ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(YearTopupStatusCardNumber req);
}
