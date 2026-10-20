package com.sanedge.paymentgateway.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionYearMethod {
    private String reportYear;
    private String paymentMethod;
    private Long totalTransactions;
    private Long totalAmount;
}
