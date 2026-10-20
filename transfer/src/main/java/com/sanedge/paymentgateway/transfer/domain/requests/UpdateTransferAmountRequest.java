package com.sanedge.paymentgateway.transfer.domain.requests;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateTransferAmountRequest {
    private String transferId;

    @Min(value = 1, message = "Transfer amount harus lebih dari 0")
    private Long transferAmount;
}
