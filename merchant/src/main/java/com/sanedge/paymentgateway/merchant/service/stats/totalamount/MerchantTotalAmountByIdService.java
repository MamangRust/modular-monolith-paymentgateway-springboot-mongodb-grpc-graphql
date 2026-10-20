package com.sanedge.paymentgateway.merchant.service.stats.totalamount;

import java.util.List;

import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearTotalAmountMerchant;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount;

public interface MerchantTotalAmountByIdService {
    ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmountById(MonthYearTotalAmountMerchant req);

    ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmountById(MonthYearTotalAmountMerchant req);
}
