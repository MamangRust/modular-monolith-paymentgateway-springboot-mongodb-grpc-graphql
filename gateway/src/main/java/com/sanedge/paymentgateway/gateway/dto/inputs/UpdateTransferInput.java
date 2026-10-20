package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateTransferInput(Long transferId, String transferFrom, String transferTo, Long transferAmount) {
}
