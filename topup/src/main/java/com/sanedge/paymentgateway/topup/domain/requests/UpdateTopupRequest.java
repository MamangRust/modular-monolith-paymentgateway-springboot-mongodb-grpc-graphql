package com.sanedge.paymentgateway.topup.domain.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTopupRequest {

    @NotBlank(message = "Card number wajib diisi")
    private String cardNumber;

    private String topupId;

    @Min(value = 50000, message = "Minimal topup 50.000")
    private Long topupAmount;

    @NotBlank(message = "Topup method wajib diisi")
    private String topupMethod;
}
