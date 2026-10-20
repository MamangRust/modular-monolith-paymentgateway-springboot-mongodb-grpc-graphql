package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionMonthMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthMethodResponse {
    private String monthName;
    private String paymentMethod;
    private Long totalTransactions;
    private Long totalAmount;

    public static TransactionMonthMethodResponse from(TransactionMonthMethod dto) {
        return TransactionMonthMethodResponse.builder()
                .monthName(dto.getMonthName())
                .paymentMethod(dto.getPaymentMethod())
                .totalTransactions(dto.getTotalTransactions())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
