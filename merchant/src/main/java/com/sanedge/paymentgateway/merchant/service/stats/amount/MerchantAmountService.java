package com.sanedge.paymentgateway.merchant.service.stats.amount;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount;

public interface MerchantAmountService {
    ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmount(Long year);

    ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmount(Long year);
}
