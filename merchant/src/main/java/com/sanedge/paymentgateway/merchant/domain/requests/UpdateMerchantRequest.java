package com.sanedge.paymentgateway.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMerchantRequest {
    private String merchantId;

    @NotBlank(message = "Nama merchant wajib diisi")
    private String name;

    private String userId;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
