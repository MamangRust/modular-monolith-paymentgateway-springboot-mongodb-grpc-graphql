package com.sanedge.paymentgateway.saldo.domain.responses.stats.total_balance;

import com.sanedge.paymentgateway.saldo.models.SaldoYearTotalBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoYearTotalBalanceResponse {
    private String reportYear;
    private Long totalBalance;

    public static SaldoYearTotalBalanceResponse from(SaldoYearTotalBalance entity) {
        return SaldoYearTotalBalanceResponse.builder()
                .reportYear(entity.getReportYear())
                .totalBalance(entity.getTotalBalance())
                .build();
    }
}
