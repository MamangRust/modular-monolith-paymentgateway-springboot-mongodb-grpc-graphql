package com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponseYearStatusSuccess {
    private String reportYear;
    private Long totalAmount;
    private Long totalSuccess;

    
}
