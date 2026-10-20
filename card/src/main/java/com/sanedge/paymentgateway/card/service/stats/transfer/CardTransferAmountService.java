package com.sanedge.paymentgateway.card.service.stats.transfer;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;

public interface CardTransferAmountService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(Long year);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(Long year);

    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(Long year);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(Long year);
}
