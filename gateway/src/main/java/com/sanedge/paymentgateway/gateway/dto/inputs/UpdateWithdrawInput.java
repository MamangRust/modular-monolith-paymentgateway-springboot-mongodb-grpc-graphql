package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateWithdrawInput(Long withdrawId, String cardNumber, Long withdrawAmount, String withdrawTime) {
}
