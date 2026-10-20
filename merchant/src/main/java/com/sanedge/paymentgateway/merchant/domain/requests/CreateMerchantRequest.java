package com.sanedge.paymentgateway.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMerchantRequest {
    @NotBlank(message = "Nama merchant wajib diisi")
    private String name;

    private String userId;
}
