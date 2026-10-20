package com.sanedge.paymentgateway.withdraw.service.stats.amount;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.YearMonthCardNumber;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawYearlyAmountResponse;

public interface WithdrawAmountByCardService {

    ApiResponse<List<WithdrawMonthlyAmountResponse>> findMonthlyByCardNumber(YearMonthCardNumber req);

    ApiResponse<List<WithdrawYearlyAmountResponse>> findYearlyByCardNumber(YearMonthCardNumber req);
}
