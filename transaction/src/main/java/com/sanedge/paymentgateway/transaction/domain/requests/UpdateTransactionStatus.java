package com.sanedge.paymentgateway.transaction.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTransactionStatus {

    private String transactionId;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
