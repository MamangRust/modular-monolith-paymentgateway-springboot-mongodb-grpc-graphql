package com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseMonthlyPaymentMethod {
    private String monthName;
    private String paymentMethod;
    private Long totalAmount;

    
}