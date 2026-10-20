package com.sanedge.paymentgateway.gateway.dto.inputs;

public record MerchantTransactionsByApiKeyInput(String apiKey, Integer page, Integer pageSize, String search) {
}
