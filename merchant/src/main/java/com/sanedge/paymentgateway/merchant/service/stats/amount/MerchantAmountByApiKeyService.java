package com.sanedge.paymentgateway.merchant.service.stats.amount;

import java.util.List;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearAmountApiKey;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount;

public interface MerchantAmountByApiKeyService {
    ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmountByApiKey(MonthYearAmountApiKey req);

    ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmountByApiKey(MonthYearAmountApiKey req);
}
