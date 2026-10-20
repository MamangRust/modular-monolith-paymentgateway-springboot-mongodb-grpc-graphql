package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateMerchantInput(Long merchantId, String name, Long userId, String status) {
}
