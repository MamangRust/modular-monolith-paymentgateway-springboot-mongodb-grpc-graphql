package com.sanedge.paymentgateway.merchant.service.stats.totalamount;

import java.util.List;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearTotalAmountApiKey;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount;

public interface MerchantTotalAmountByApiKeyService {
    ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmountByApiKey(MonthYearTotalAmountApiKey req);

    ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmountByApiKey(MonthYearTotalAmountApiKey req);
}
