package com.sanedge.paymentgateway.saldo.service.stats;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.saldo.domain.requests.MonthTotalSaldoBalance;
import com.sanedge.paymentgateway.saldo.domain.responses.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.paymentgateway.saldo.domain.responses.stats.total_balance.SaldoYearTotalBalanceResponse;

public interface SaldoTotalAmountService {
    ApiResponse<List<SaldoMonthTotalBalanceResponse>> findMonthTotalBalance(MonthTotalSaldoBalance req);

    ApiResponse<List<SaldoYearTotalBalanceResponse>> findYearTotalBalance(Long year);
}
