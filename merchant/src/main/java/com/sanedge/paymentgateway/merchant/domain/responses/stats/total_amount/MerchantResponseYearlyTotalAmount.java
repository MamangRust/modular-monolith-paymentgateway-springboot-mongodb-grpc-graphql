package com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount;

import com.sanedge.paymentgateway.merchant.models.MerchantYearlyTotalAmount;

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

    public static MerchantResponseYearlyTotalAmount from(MerchantYearlyTotalAmount model) {
        return MerchantResponseYearlyTotalAmount.builder()
                .reportYear(model.getReportYear())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}