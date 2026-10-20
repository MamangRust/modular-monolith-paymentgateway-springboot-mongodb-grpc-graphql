package com.sanedge.paymentgateway.transfer.domain.responses;

import com.sanedge.paymentgateway.transfer.models.TransferYearAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferYearAmountResponse {
    private String reportYear;
    private Long totalAmount;

    public static TransferYearAmountResponse from(TransferYearAmount dto) {
        return TransferYearAmountResponse.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
