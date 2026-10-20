package com.sanedge.paymentgateway.saldo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoYearTotalBalance {
    private String reportYear;
    private Long totalBalance;
}
