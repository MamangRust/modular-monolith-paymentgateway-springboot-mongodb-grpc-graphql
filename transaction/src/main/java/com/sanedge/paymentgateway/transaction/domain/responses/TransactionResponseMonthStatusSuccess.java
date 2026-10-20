package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseMonthStatusSuccess {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
    private Long totalSuccess;

    public static TransactionResponseMonthStatusSuccess from(TransactionMonthStatusSuccess dto) {
        return TransactionResponseMonthStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
