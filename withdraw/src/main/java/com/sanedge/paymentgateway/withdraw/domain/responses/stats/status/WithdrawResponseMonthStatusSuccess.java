package com.sanedge.paymentgateway.withdraw.domain.responses.stats.status;

import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseMonthStatusSuccess {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
    private Long totalSuccess;

    public static WithdrawResponseMonthStatusSuccess from(WithdrawMonthStatusSuccess dto) {
        return WithdrawResponseMonthStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
