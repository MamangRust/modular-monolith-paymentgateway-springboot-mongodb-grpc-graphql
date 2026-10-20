package com.sanedge.paymentgateway.topup.domain.responses.stats.amount;

import com.sanedge.paymentgateway.topup.models.TopupMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupMonthAmountResponse {
    private String monthName;
    private Long totalAmount;

    public static TopupMonthAmountResponse from(TopupMonthAmount dto) {
        return TopupMonthAmountResponse.builder()
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
