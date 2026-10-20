package com.sanedge.paymentgateway.card.service.stats.transaction;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount;

public interface CardTransactionAmountService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmount(Long year);

    ApiResponse<List<CardResponseYearAmount>> findYearAmount(Long year);
}
