package com.sanedge.paymentgateway.withdraw.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthStatusWithdraw {

    @Min(value = 2000, message = "Tahun tidak valid (minimal 2000)")
    @Max(value = 2100, message = "Tahun tidak valid (maksimal 2100)")
    private Long year;

    @Min(value = 1, message = "Bulan harus antara 1 - 12")
    @Max(value = 12, message = "Bulan harus antara 1 - 12")
    private int month;
}
