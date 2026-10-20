package com.sanedge.paymentgateway.gateway.dto.inputs;

public record WithdrawCardNumberMonthInput(String cardNumber, Integer year, Integer month) {
}
