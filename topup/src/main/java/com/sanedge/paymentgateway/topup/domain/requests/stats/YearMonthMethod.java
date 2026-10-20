package com.sanedge.paymentgateway.topup.domain.requests.stats;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YearMonthMethod {

    @NotBlank(message = "Card number wajib diisi")
    private String cardNumber;

    @Min(value = 2000, message = "Tahun tidak valid")
    @Max(value = 2100, message = "Tahun tidak valid")
    private Long year;
}
