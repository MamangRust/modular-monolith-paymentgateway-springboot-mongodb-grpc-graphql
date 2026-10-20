package com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseMonthStatusSuccess {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
    private Long totalSuccess;

    
}