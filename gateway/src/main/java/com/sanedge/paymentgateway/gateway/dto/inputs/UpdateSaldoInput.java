package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateSaldoInput(Long saldoId, String cardNumber, Long totalBalance) {
}
