package com.sanedge.paymentgateway.topup.domain.responses.stats.status;

import com.sanedge.paymentgateway.topup.models.TopupYearStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponseYearStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private Long totalFailed;

    public static TopupResponseYearStatusFailed from(TopupYearStatusFailed dto) {
        return TopupResponseYearStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
