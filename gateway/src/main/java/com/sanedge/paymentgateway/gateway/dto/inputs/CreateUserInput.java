package com.sanedge.paymentgateway.gateway.dto.inputs;

public record CreateUserInput(String username, String firstname, String lastname, String email, String password, String confirmPassword) {
}
