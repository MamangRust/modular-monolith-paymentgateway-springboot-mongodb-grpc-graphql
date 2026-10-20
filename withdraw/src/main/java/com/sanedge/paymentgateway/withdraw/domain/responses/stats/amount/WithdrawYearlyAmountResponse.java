package com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount;

import com.sanedge.paymentgateway.withdraw.models.WithdrawYearlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawYearlyAmountResponse {
    private String reportYear;
    private Long totalAmount;

    public static WithdrawYearlyAmountResponse from(WithdrawYearlyAmount dto) {
        return WithdrawYearlyAmountResponse.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
