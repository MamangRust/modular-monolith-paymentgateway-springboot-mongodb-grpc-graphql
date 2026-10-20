package com.sanedge.paymentgateway.transfer.domain.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YearStatusTransferCardNumber {
    @NotBlank(message = "Card number wajib diisi")
    private String cardNumber;

    @Min(value = 2000, message = "Tahun tidak valid")
    @Max(value = 2100, message = "Tahun tidak valid")
    private int year;
}
