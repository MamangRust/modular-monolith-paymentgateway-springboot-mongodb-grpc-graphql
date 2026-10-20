package com.sanedge.paymentgateway.merchant.service.stats.method;

import java.util.List;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearPaymentMethodMerchant;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod;

public interface MerchantMethodByIdService {
    ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethodById(MonthYearPaymentMethodMerchant req);

    ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethodById(MonthYearPaymentMethodMerchant req);
}
