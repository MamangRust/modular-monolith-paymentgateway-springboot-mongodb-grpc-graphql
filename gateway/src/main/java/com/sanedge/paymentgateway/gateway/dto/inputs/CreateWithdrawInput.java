package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateWithdrawInput(String cardNumber, Long withdrawAmount, String withdrawTime) {
}
