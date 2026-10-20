package com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.total_balance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoMonthTotalBalanceResponse {
    private String monthName;
    private String reportYear;
    private Long totalBalance;

    
}