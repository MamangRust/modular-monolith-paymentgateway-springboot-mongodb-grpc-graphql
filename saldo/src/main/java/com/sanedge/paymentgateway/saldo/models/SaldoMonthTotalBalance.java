package com.sanedge.paymentgateway.saldo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoMonthTotalBalance {
    private String reportYear;
    private String monthName;
    private Long totalBalance;
}
