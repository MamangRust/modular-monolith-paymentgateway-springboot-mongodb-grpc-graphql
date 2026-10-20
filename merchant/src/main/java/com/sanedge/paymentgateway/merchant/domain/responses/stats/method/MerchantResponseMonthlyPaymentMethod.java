package com.sanedge.paymentgateway.merchant.domain.responses.stats.method;

import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyPaymentMethod;

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

    public static MerchantResponseMonthlyPaymentMethod from(MerchantMonthlyPaymentMethod model) {
        return MerchantResponseMonthlyPaymentMethod.builder()
                .monthName(model.getMonthName())
                .paymentMethod(model.getPaymentMethod())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}