package com.sanedge.paymentgateway.merchant.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Port of the blueprint's {@code CreateMerchantDocumentRequest}. */
@Data
public class CreateMerchantDocumentRequest {

    @NotBlank(message = "merchantId wajib diisi")
    private String merchantId;

    @NotBlank(message = "Document type wajib diisi")
    private String documentType;

    @NotBlank(message = "Document url wajib diisi")
    private String documentUrl;
}
