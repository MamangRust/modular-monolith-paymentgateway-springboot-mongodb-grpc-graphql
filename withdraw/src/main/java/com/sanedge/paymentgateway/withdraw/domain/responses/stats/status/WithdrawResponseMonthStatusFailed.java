package com.sanedge.paymentgateway.withdraw.domain.responses.stats.status;

import com.sanedge.paymentgateway.withdraw.models.WithdrawMonthStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseMonthStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private String monthName;
    private Long totalFailed;

    public static WithdrawResponseMonthStatusFailed from(WithdrawMonthStatusFailed dto) {
        return WithdrawResponseMonthStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
