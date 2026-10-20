package com.sanedge.paymentgateway.topup.domain.responses.stats.status;

import com.sanedge.paymentgateway.topup.models.TopupYearStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponseYearStatusSuccess {
    private String reportYear;
    private Long totalAmount;
    private Long totalSuccess;

    public static TopupResponseYearStatusSuccess from(TopupYearStatusSuccess dto) {
        return TopupResponseYearStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
