package com.sanedge.paymentgateway.topup.domain.responses.stats.status;

import com.sanedge.paymentgateway.topup.models.TopupMonthStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopupResponseMonthStatusSuccess {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
    private Long totalSuccess;

    public static TopupResponseMonthStatusSuccess from(TopupMonthStatusSuccess dto) {
        return TopupResponseMonthStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
