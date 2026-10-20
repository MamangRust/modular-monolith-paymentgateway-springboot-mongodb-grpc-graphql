package com.sanedge.paymentgateway.merchant.service.stats.totalamount;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount;

public interface MerchantTotalAmountService {
    ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmount(Long year);

    ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmount(Long year);
}