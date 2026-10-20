package com.sanedge.paymentgateway.gateway.dto.responses.card.stats.balance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseMonthBalance {
    private String monthName;
    private Long totalBalance;

    
}