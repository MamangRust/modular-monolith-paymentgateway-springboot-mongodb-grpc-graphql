package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthAmountResponse {
    private String monthName;
    private Long totalAmount;

    public static TransactionMonthAmountResponse from(TransactionMonthAmount dto) {
        return TransactionMonthAmountResponse.builder()
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
