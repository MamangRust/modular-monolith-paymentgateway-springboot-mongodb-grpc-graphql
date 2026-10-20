package com.sanedge.paymentgateway.topup.domain.responses.stats.method;

import com.sanedge.paymentgateway.topup.models.TopupMonthMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupMonthMethodResponse {
    private String monthName;
    private String topupMethod;
    private Long totalTopups;
    private Long totalAmount;

    public static TopupMonthMethodResponse from(TopupMonthMethod dto) {
        return TopupMonthMethodResponse.builder()
                .monthName(dto.getMonthName())
                .topupMethod(dto.getTopupMethod())
                .totalTopups(dto.getTotalTopups())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
