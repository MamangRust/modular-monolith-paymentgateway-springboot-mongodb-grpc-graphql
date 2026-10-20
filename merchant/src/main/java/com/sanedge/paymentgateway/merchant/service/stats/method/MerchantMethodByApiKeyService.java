package com.sanedge.paymentgateway.merchant.service.stats.method;

import java.util.List;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearPaymentMethodApiKey;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod;

public interface MerchantMethodByApiKeyService {
    ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethod(MonthYearPaymentMethodApiKey req);

    ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethod(MonthYearPaymentMethodApiKey req);
}
