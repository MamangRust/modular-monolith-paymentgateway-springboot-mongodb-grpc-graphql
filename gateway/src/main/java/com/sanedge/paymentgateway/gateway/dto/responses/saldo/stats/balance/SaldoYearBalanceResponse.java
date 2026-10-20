package com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.balance;
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

    
}