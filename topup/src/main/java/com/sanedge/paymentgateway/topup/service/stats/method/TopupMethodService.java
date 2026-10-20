package com.sanedge.paymentgateway.topup.service.stats.method;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupMonthMethodResponse;
import com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupYearlyMethodResponse;

public interface TopupMethodService {
    ApiResponse<List<TopupMonthMethodResponse>> findMonthlyMethods(Long year);

    ApiResponse<List<TopupYearlyMethodResponse>> findYearlyMethods(Long year);
}
