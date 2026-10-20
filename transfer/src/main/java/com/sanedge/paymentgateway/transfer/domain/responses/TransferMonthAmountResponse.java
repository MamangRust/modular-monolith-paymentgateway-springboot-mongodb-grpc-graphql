package com.sanedge.paymentgateway.transfer.domain.responses;

import com.sanedge.paymentgateway.transfer.models.TransferMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMonthAmountResponse {
    private String monthName;
    private Long totalAmount;

    public static TransferMonthAmountResponse from(TransferMonthAmount dto) {
        return TransferMonthAmountResponse.builder()
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
