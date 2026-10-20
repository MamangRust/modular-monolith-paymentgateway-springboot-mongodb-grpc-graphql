package com.sanedge.paymentgateway.transfer.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTransferStatus {
    private String transferId;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
