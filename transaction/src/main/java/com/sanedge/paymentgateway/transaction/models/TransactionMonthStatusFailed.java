package com.sanedge.paymentgateway.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionMonthStatusFailed {
    private String reportYear;
    private String monthName;
    private Long totalFailed;
    private Long totalAmount;
}
