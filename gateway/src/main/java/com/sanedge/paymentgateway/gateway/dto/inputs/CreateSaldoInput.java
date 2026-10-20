package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateSaldoInput(String cardNumber, Long totalBalance) {
}
