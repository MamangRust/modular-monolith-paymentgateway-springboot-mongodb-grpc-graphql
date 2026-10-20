package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionMonthStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseMonthStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private String monthName;
    private Long totalFailed;

    public static TransactionResponseMonthStatusFailed from(TransactionMonthStatusFailed dto) {
        return TransactionResponseMonthStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
