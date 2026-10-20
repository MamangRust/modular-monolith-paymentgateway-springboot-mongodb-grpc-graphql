package com.sanedge.paymentgateway.gateway.dto.inputs;

public record RegisterInput(String firstname, String lastname, String email, String password, String confirmPassword) {
}
