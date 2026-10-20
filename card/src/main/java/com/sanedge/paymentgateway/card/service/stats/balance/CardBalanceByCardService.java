package com.sanedge.paymentgateway.card.service.stats.balance;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;

import java.util.List;

import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseMonthBalance;
import com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseYearBalance;

public interface CardBalanceByCardService {
    ApiResponse<List<CardResponseMonthBalance>> findMonthBalanceByCard(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearBalance>> findYearBalanceByCard(MonthYearCardNumberCard req);
}
