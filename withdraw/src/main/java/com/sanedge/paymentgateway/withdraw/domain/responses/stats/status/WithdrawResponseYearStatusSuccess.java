package com.sanedge.paymentgateway.withdraw.domain.responses.stats.status;

import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseYearStatusSuccess {
    private String reportYear;
    private Long totalAmount;
    private Long totalSuccess;

    public static WithdrawResponseYearStatusSuccess from(WithdrawYearStatusSuccess dto) {
        return WithdrawResponseYearStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
