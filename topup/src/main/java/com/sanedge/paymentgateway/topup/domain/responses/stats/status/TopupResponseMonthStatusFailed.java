package com.sanedge.paymentgateway.topup.domain.responses.stats.status;

import com.sanedge.paymentgateway.topup.models.TopupMonthStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponseMonthStatusFailed {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
    private Long totalFailed;

    public static TopupResponseMonthStatusFailed from(TopupMonthStatusFailed dto) {
        return TopupResponseMonthStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
