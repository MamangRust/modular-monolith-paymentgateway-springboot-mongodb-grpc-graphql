package com.sanedge.paymentgateway.topup.domain.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTopupStatus {

    private String topupId;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
