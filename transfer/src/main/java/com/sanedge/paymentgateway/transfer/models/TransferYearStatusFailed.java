package com.sanedge.paymentgateway.transfer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferYearStatusFailed {
    private String reportYear;
    private Long totalFailed;
    private Long totalAmount;
}
