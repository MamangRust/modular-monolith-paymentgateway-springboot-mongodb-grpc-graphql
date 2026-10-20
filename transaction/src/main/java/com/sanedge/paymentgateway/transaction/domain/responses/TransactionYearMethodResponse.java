package com.sanedge.paymentgateway.transaction.domain.responses;

import com.sanedge.paymentgateway.transaction.models.TransactionYearMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearMethodResponse {
    private String reportYear;
    private String paymentMethod;
    private Long totalTransactions;
    private Long totalAmount;

    public static TransactionYearMethodResponse from(TransactionYearMethod dto) {
        return TransactionYearMethodResponse.builder()
                .reportYear(dto.getReportYear())
                .paymentMethod(dto.getPaymentMethod())
                .totalTransactions(dto.getTotalTransactions())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
