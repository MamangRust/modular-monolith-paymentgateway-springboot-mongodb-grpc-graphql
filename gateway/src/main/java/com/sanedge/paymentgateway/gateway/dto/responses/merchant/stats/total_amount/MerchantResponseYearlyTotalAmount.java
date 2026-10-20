package com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.total_amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseYearlyTotalAmount {
    private String reportYear;
    private Long totalAmount;

    
}