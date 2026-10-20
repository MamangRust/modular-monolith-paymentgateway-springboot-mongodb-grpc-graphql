package com.sanedge.paymentgateway.transfer.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MonthStatusTransfer {
    @Min(value = 2000, message = "Tahun tidak valid")
    @Max(value = 2100, message = "Tahun tidak valid")
    private int year;

    @Min(value = 1, message = "Bulan harus antara 1 - 12")
    @Max(value = 12, message = "Bulan harus antara 1 - 12")
    private int month;
}
