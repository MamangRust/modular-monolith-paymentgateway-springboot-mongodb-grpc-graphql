package com.sanedge.paymentgateway.transaction.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthAmount {
    private String monthName;
    private Long totalAmount;
}
