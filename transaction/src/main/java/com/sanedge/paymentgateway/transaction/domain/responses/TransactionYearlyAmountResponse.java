package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionYearAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearlyAmountResponse {
    private String reportYear;
    private Long totalAmount;

    public static TransactionYearlyAmountResponse from(TransactionYearAmount dto) {
        return TransactionYearlyAmountResponse.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
