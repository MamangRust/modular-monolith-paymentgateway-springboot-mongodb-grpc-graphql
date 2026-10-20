package com.sanedge.paymentgateway.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionYearStatusSuccess {
    private String reportYear;
    private Long totalSuccess;
    private Long totalAmount;
}
