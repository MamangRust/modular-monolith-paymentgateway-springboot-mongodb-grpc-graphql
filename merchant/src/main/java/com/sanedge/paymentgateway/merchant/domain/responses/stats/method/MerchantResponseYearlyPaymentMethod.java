package com.sanedge.paymentgateway.merchant.domain.responses.stats.method;

import com.sanedge.paymentgateway.merchant.models.MerchantYearlyPaymentMethod;

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

    public static MerchantResponseYearlyPaymentMethod from(MerchantYearlyPaymentMethod model) {
        return MerchantResponseYearlyPaymentMethod.builder()
                .reportYear(model.getReportYear())
                .paymentMethod(model.getPaymentMethod())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}
