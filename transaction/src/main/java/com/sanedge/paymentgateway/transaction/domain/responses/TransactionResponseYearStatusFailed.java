package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionYearStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseYearStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private Long totalFailed;

    public static TransactionResponseYearStatusFailed from(TransactionYearStatusFailed dto) {
        return TransactionResponseYearStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
