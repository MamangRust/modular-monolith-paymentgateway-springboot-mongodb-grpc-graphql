package com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseMonthStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private String monthName;
    private Long totalFailed;

    
}