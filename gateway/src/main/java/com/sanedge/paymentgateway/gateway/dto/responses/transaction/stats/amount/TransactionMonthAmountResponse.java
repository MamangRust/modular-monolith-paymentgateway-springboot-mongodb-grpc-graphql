package com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthAmountResponse {
    private String monthName;
    private Long totalAmount;

    
}