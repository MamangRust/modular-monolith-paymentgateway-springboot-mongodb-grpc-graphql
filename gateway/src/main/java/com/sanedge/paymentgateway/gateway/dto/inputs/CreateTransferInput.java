package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateTransferInput(String transferFrom, String transferTo, Long transferAmount) {
}
