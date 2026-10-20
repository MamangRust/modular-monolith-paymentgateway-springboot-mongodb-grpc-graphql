package com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount;

import com.sanedge.paymentgateway.merchant.models.MerchantMonthlyTotalAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseMonthlyTotalAmount {
    private String reportYear;
    private String monthName;
    private Long totalAmount;

    public static MerchantResponseMonthlyTotalAmount from(MerchantMonthlyTotalAmount model) {
        return MerchantResponseMonthlyTotalAmount.builder()
                .reportYear(model.getReportYear())
                .monthName(model.getMonthName())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}
