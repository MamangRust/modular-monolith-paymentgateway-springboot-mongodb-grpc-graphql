package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateTopupInput(String cardNumber, Long topupAmount, String topupMethod) {
}
