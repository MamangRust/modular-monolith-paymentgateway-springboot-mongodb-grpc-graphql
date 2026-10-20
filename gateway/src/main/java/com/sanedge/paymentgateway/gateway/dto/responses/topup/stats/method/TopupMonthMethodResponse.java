package com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupMonthMethodResponse {
    private String monthName;
    private String topupMethod;
    private Long totalTopups;
    private Long totalAmount;

    
}