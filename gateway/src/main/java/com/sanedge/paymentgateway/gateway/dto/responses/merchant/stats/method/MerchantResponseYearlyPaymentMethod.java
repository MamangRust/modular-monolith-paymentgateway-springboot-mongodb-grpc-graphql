package com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseYearlyPaymentMethod {
    private String reportYear;
    private String paymentMethod;
    private Long totalAmount;

    
}
