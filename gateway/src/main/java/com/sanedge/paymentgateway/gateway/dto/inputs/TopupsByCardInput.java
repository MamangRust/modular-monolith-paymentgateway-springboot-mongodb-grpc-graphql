package com.sanedge.paymentgateway.gateway.dto.inputs;

public record TopupsByCardInput(String cardNumber, Integer page, Integer pageSize, String search) {
}
