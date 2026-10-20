package com.sanedge.paymentgateway.card.service.stats.transaction;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;

public interface CardTransactionAmountByCardService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountByCard(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountByCard(MonthYearCardNumberCard req);
}
