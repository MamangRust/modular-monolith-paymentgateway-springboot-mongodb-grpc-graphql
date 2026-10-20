package com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearMethodResponse {
    private String reportYear;
    private String paymentMethod;
    private Long totalTransactions;
    private Long totalAmount;

    
}
