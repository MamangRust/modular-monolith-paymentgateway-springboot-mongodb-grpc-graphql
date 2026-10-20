package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateTransactionInput(String cardNumber, Long amount, String paymentMethod, Long merchantId, String transactionTime) {
}
