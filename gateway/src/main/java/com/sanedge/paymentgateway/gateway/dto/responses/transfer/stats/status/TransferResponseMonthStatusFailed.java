package com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseMonthStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private String monthName;
    private Long totalFailed;

    
}