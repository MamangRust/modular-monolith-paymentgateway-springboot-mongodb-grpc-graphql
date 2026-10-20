package com.sanedge.paymentgateway.saldo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoYearBalance {
    private String reportYear;
    private Long totalBalance;
}
