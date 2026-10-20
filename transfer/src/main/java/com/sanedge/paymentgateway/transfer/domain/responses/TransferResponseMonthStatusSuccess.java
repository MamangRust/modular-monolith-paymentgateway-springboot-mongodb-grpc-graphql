package com.sanedge.paymentgateway.transfer.domain.responses;

import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseMonthStatusSuccess {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
    private Long totalSuccess;

    public static TransferResponseMonthStatusSuccess from(TransferMonthStatusSuccess dto) {
        return TransferResponseMonthStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
