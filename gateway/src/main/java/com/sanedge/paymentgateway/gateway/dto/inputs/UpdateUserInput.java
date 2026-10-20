package com.sanedge.paymentgateway.gateway.dto.inputs;

public record UpdateUserInput(Long id, String username, String firstname, String lastname, String email, String password, String confirmPassword) {
}
