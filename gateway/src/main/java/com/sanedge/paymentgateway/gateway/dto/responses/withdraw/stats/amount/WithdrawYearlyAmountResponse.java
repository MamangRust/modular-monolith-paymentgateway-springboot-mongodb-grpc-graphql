package com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawYearlyAmountResponse {
    private String reportYear;
    private Long totalAmount;

    
}