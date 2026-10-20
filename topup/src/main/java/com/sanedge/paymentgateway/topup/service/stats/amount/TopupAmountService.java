package com.sanedge.paymentgateway.topup.service.stats.amount;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupMonthAmountResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupYearlyAmountResponse;

public interface TopupAmountService {
    ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(Long year);

    ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(Long year);
}
