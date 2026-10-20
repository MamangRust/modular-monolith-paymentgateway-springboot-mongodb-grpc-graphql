package com.sanedge.paymentgateway.merchant.service.stats.method;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod;

public interface MerchantMethodService {
    ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethod(Long year);

    ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethod(Long year);
}
