package com.sanedge.paymentgateway.auth.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRefreshToken {

    private String userId;

    @NotBlank(message = "Token wajib diisi")
    private String token;

    @NotBlank(message = "ExpiresAt wajib diisi")
    private String expiresAt;
}
