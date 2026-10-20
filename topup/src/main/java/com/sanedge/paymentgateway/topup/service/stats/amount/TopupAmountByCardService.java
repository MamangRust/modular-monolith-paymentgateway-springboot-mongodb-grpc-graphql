package com.sanedge.paymentgateway.topup.service.stats.amount;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.topup.domain.requests.stats.YearMonthMethod;
import com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupMonthAmountResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupYearlyAmountResponse;

public interface TopupAmountByCardService {

    ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(YearMonthMethod req);

    ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(YearMonthMethod req);
}
