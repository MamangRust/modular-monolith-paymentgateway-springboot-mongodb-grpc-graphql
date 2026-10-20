package com.sanedge.paymentgateway.gateway.dto.inputs;

public record MerchantTransactionsByIdInput(Long merchantId, Integer page, Integer pageSize, String search) {
}
