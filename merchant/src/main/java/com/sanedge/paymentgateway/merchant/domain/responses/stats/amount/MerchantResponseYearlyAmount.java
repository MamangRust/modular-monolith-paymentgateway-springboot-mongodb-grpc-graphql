package com.sanedge.paymentgateway.merchant.domain.responses.stats.amount;

import com.sanedge.paymentgateway.merchant.models.MerchantYearlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseYearlyAmount {
    private String reportYear;
    private Long totalAmount;

    public static MerchantResponseYearlyAmount from(MerchantYearlyAmount model) {
        return MerchantResponseYearlyAmount.builder()
                .reportYear(model.getReportYear())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}