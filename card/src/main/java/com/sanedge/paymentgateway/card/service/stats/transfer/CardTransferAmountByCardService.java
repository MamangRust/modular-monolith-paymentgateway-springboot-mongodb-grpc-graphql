package com.sanedge.paymentgateway.card.service.stats.transfer;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;

public interface CardTransferAmountByCardService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(MonthYearCardNumberCard req);
}
