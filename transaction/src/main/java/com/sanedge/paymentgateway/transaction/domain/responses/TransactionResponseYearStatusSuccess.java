package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseYearStatusSuccess {
    private String reportYear;
    private Long totalAmount;
    private Long totalSuccess;

    public static TransactionResponseYearStatusSuccess from(TransactionYearStatusSuccess dto) {
        return TransactionResponseYearStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
