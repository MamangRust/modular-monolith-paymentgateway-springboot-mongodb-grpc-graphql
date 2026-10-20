package com.sanedge.paymentgateway.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionYearStatusFailed {
    private String reportYear;
    private Long totalFailed;
    private Long totalAmount;
}
