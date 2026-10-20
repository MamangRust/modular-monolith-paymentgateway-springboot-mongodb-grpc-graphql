package com.sanedge.paymentgateway.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Port of the blueprint's {@code UpdateMerchantDocumentStatus}. */
@Data
public class UpdateMerchantDocumentStatus {

    @NotBlank(message = "documentId wajib diisi")
    private String documentId;

    @NotBlank(message = "merchantId wajib diisi")
    private String merchantId;

    private String note;

    @NotBlank(message = "Status wajib diisi")
    private String status;
}
