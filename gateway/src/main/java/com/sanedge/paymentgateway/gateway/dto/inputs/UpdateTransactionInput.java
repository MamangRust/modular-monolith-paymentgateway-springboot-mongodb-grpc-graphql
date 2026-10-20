package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateTransactionInput(Long transactionId, String cardNumber, Long amount, String paymentMethod, Long merchantId, String transactionTime) {
}
