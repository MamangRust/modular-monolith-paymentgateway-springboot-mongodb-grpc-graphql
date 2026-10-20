package com.sanedge.paymentgateway.gateway.resolver;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.gateway.dto.inputs.LoginInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.RefreshTokenInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.RegisterInput;
import com.sanedge.paymentgateway.gateway.dto.responses.auth.TokenResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.user.UserResponse;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.gateway.security.UserDetailsImpl;
import com.sanedge.paymentgateway.pb.AuthServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthResolver {

    private final AuthServiceGrpc.AuthServiceBlockingStub authStub;

    @QueryMapping
    public String healthchecker() {
        return "API is up and running!";
    }

    @QueryMapping
    public ApiResponse<UserResponse> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        int userId = 0;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl details) {
            userId = details.getId() == null ? 0 : details.getId().intValue();
        }
        var resp = authStub.getMe(com.sanedge.paymentgateway.pb.GetMeRequest.newBuilder()
                .setUserId(userId)
                .build());
        return ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build();
    }

    @MutationMapping
    public ApiResponse<UserResponse> register(@Argument RegisterInput input) {
        var resp = authStub.registerUser(com.sanedge.paymentgateway.pb.RegisterRequest.newBuilder()
                .setFirstname(nvl(input != null ? input.firstname() : null))
                .setLastname(nvl(input != null ? input.lastname() : null))
                .setEmail(nvl(input != null ? input.email() : null))
                .setPassword(nvl(input != null ? input.password() : null))
                .setConfirmPassword(nvl(input != null ? input.confirmPassword() : null))
                .build());
        return ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build();
    }

    @MutationMapping
    public ApiResponse<TokenResponse> login(@Argument LoginInput input) {
        var resp = authStub.loginUser(com.sanedge.paymentgateway.pb.LoginRequest.newBuilder()
                .setEmail(nvl(input != null ? input.username() : null))
                .setPassword(nvl(input != null ? input.password() : null))
                .build());
        return ApiResponse.<TokenResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.token(resp.getData()))
                .build();
    }

    @MutationMapping
    public ApiResponse<TokenResponse> refreshToken(@Argument RefreshTokenInput input) {
        var resp = authStub.refreshToken(com.sanedge.paymentgateway.pb.RefreshTokenRequest.newBuilder()
                .setRefreshToken(nvl(input != null ? input.refreshToken() : null))
                .build());
        return ApiResponse.<TokenResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.token(resp.getData()))
                .build();
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }
}
