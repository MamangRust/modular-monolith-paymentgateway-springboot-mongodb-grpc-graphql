package com.sanedge.paymentgateway.topup.domain.responses.stats.method;

import com.sanedge.paymentgateway.topup.models.TopupYearMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupYearlyMethodResponse {
    private String reportYear;
    private String topupMethod;
    private Long totalTopups;
    private Long totalAmount;

    public static TopupYearlyMethodResponse from(TopupYearMethod dto) {
        return TopupYearlyMethodResponse.builder()
                .reportYear(dto.getReportYear())
                .topupMethod(dto.getTopupMethod())
                .totalTopups(dto.getTotalTopups())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
