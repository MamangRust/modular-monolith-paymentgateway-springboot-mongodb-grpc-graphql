package com.sanedge.paymentgateway.merchant.domain.responses.stats.amount;

import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseMonthlyAmount {
    private String monthName;
    private Long totalAmount;

    public static MerchantResponseMonthlyAmount from(MerchantMonthlyAmount model) {
        return MerchantResponseMonthlyAmount.builder()
                .monthName(model.getMonthName())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}