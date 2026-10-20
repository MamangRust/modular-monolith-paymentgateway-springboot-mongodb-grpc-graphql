package com.sanedge.paymentgateway.withdraw.domain.responses.stats.status;

import com.sanedge.paymentgateway.withdraw.models.WithdrawYearStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponseYearStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private Long totalFailed;

    public static WithdrawResponseYearStatusFailed from(WithdrawYearStatusFailed dto) {
        return WithdrawResponseYearStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
