package com.sanedge.paymentgateway.role.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    private String id;

    @NotBlank(message = "Nama role wajib diisi")
    private String name;
}
