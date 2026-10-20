package com.sanedge.paymentgateway.withdraw.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateWithdrawStatus {
    @NotBlank(message = "Withdraw ID wajib diisi")
    private String withdrawId;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
