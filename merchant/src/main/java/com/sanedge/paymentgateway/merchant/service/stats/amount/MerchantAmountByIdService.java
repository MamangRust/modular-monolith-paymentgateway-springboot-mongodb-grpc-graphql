package com.sanedge.paymentgateway.merchant.service.stats.amount;

import java.util.List;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearAmountMerchant;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount;

public interface MerchantAmountByIdService {
    ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmountById(MonthYearAmountMerchant req);

    ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmountById(MonthYearAmountMerchant req);
}
