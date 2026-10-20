package com.sanedge.paymentgateway.saldo.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MonthTotalSaldoBalance {

    @Min(value = 1900, message = "Tahun tidak valid")
    @Max(value = 2100, message = "Tahun tidak valid")
    private Long year;

    @Min(value = 1, message = "Bulan harus >= 1")
    @Max(value = 12, message = "Bulan harus <= 12")
    private int month;
}
