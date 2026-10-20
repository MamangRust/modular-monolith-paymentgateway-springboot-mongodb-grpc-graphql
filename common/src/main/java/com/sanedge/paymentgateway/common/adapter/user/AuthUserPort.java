package com.sanedge.paymentgateway.common.adapter.user;

import java.util.Optional;

import com.sanedge.paymentgateway.common.adapter.model.User;

/**
 * Auth-facing user surface: credential lookups plus the account mutations the
 * auth flows perform. Replaces direct {@code @GrpcClient("user")} usage in the
 * auth service.
 */
public interface AuthUserPort {

    User findById(String userId);

    Optional<User> findByEmail(String email);

    User create(RegisterData data);

    VerifyResult verifyPassword(String email, String password);

    User update(UpdateData data);

    record RegisterData(
            String firstname,
            String lastname,
            String email,
            String password,
            String confirmPassword) {
    }

    record UpdateData(
            String id,
            String firstname,
            String lastname,
            String email,
            String password,
            String confirmPassword) {
    }

    record VerifyResult(boolean valid, User user) {
    }
}
