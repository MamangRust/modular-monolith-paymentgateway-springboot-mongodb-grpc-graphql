package com.sanedge.paymentgateway.gateway.dto.responses.card.stats.balance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseYearBalance {
    private String reportYear;
    private Long totalBalance;

    
}
