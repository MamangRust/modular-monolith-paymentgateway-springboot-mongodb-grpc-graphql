package com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseYearStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private Long totalFailed;

    
}