package com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupYearlyMethodResponse {
    private String reportYear;
    private String topupMethod;
    private Long totalTopups;
    private Long totalAmount;

    
}