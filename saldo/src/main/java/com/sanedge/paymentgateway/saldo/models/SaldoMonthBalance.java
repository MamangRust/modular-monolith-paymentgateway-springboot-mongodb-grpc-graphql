package com.sanedge.paymentgateway.saldo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoMonthBalance {
    private String monthName;
    private Long totalBalance;
}
