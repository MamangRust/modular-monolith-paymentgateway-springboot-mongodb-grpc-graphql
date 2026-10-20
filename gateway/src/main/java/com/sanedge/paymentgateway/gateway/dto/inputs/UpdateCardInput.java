package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateCardInput(Long cardId, Long userId, String cardType, String expireDate, String cvv, String cardProvider) {
}
