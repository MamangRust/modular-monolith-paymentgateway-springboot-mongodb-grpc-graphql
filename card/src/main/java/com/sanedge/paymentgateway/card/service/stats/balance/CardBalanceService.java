package com.sanedge.paymentgateway.card.service.stats.balance;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseMonthBalance;
import com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseYearBalance;

public interface CardBalanceService {
    ApiResponse<List<CardResponseMonthBalance>> findMonthBalance(Long year);

    ApiResponse<List<CardResponseYearBalance>> findYearBalance(Long year);
}
