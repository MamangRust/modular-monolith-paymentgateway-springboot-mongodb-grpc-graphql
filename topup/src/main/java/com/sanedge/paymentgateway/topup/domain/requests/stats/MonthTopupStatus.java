package com.sanedge.paymentgateway.topup.domain.requests.stats;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MonthTopupStatus {

    @Min(value = 2000, message = "Tahun tidak valid")
    @Max(value = 2100, message = "Tahun tidak valid")
    private Long year;

    @Min(value = 1, message = "Bulan harus >= 1")
    @Max(value = 12, message = "Bulan harus <= 12")
    private int month;
}
