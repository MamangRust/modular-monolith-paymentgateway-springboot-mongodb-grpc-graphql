package com.sanedge.paymentgateway.saldo.service.stats;

import java.util.List;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.saldo.domain.responses.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.paymentgateway.saldo.domain.responses.stats.balance.SaldoYearBalanceResponse;

public interface SaldoBalanceService {
    ApiResponse<List<SaldoMonthBalanceResponse>> getMonthBalance(Long year);

    ApiResponse<List<SaldoYearBalanceResponse>> getYearBalance(Long year);
}
