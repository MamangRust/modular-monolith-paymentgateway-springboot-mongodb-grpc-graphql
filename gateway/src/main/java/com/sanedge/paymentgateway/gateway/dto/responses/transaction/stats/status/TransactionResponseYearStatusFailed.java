package com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseYearStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private Long totalFailed;

    
}