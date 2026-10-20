package com.sanedge.paymentgateway.transfer.domain.responses;

import com.sanedge.paymentgateway.transfer.models.TransferMonthStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseMonthStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private String monthName;
    private Long totalFailed;

    public static TransferResponseMonthStatusFailed from(TransferMonthStatusFailed dto) {
        return TransferResponseMonthStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}
