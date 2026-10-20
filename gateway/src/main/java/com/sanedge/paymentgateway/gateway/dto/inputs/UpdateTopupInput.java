package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateTopupInput(Long topupId, String cardNumber, Long topupAmount, String topupMethod) {
}
