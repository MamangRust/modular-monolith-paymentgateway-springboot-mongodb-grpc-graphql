package com.sanedge.paymentgateway.transfer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMonthAmount {
    private String monthName;
    private Long totalAmount;
}
