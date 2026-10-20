package com.sanedge.paymentgateway.saldo.domain.responses.stats.balance;

import com.sanedge.paymentgateway.saldo.models.SaldoYearBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoYearBalanceResponse {
    private String reportYear;
    private Long totalBalance;

    public static SaldoYearBalanceResponse from(SaldoYearBalance entity) {
        return SaldoYearBalanceResponse.builder()
                .reportYear(entity.getReportYear())
                .totalBalance(entity.getTotalBalance())
                .build();
    }
}
