package com.sanedge.paymentgateway.transfer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferYearStatusSuccess {
    private String reportYear;
    private Long totalSuccess;
    private Long totalAmount;
}
