package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateCardInput(Long userId, String cardType, String expireDate, String cvv, String cardProvider) {
}
