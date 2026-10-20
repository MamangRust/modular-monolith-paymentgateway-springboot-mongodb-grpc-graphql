package com.sanedge.paymentgateway.gateway.dto.responses.card.stats.amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseMonthAmount {
    private String monthName;
    private Long totalAmount;

    
}