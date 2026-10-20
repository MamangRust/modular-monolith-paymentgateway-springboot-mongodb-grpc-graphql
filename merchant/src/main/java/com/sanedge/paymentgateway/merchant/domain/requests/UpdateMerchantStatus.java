package com.sanedge.paymentgateway.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMerchantStatus {
    private String merchantId;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
