package com.sanedge.paymentgateway.gateway.dto.inputs;

public record TransactionsByCardInput(String cardNumber, Integer page, Integer pageSize, String search) {
}
