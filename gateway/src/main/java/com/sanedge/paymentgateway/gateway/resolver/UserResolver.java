package com.sanedge.paymentgateway.gateway.resolver;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.google.protobuf.Empty;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateUserInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateUserInput;
import com.sanedge.paymentgateway.gateway.dto.responses.user.UserResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.user.UserResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.user.UserCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.user.UserQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub;
    private final UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<UserResponse>> users(@Argument PageInput input) {
        var resp = userQueryStub.findAll(com.sanedge.paymentgateway.pb.user.FindAllUserRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<UserResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.users(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<UserResponse> user(@Argument Long id) {
        var resp = userQueryStub.findById(com.sanedge.paymentgateway.pb.user.FindByIdUserRequest.newBuilder()
                .setId(nvl(id))
                .build());
        return ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<UserResponseDeleteAt>> activeUsers(@Argument PageInput input) {
        var resp = userQueryStub.findByActive(com.sanedge.paymentgateway.pb.user.FindAllUserRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.usersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<UserResponseDeleteAt>> trashedUsers(@Argument PageInput input) {
        var resp = userQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.user.FindAllUserRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.usersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserResponse> createUser(@Argument CreateUserInput input) {
        var resp = userCommandStub.create(com.sanedge.paymentgateway.pb.user.CreateUserRequest.newBuilder()
                .setFirstname(nvl(input.firstname()))
                .setLastname(nvl(input.lastname()))
                .setEmail(nvl(input.email()))
                .setPassword(nvl(input.password()))
                .setConfirmPassword(nvl(input.confirmPassword()))
                .build());
        return ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserResponse> updateUser(@Argument UpdateUserInput input) {
        var resp = userCommandStub.update(com.sanedge.paymentgateway.pb.user.UpdateUserRequest.newBuilder()
                .setId(nvl(input.id()))
                .setFirstname(nvl(input.firstname()))
                .setLastname(nvl(input.lastname()))
                .setEmail(nvl(input.email()))
                .setPassword(nvl(input.password()))
                .setConfirmPassword(nvl(input.confirmPassword()))
                .build());
        return ApiResponse.<UserResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.user(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserResponseDeleteAt> trashUser(@Argument Long id) {
        var resp = userCommandStub.trashedUser(com.sanedge.paymentgateway.pb.user.FindByIdUserRequest.newBuilder()
                .setId(nvl(id))
                .build());
        return ApiResponse.<UserResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.userDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserResponseDeleteAt> restoreUser(@Argument Long id) {
        var resp = userCommandStub.restoreUser(com.sanedge.paymentgateway.pb.user.FindByIdUserRequest.newBuilder()
                .setId(nvl(id))
                .build());
        return ApiResponse.<UserResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.userDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteUserPermanent(@Argument Long id) {
        var resp = userCommandStub.deleteUserPermanent(com.sanedge.paymentgateway.pb.user.FindByIdUserRequest.newBuilder()
                .setId(nvl(id))
                .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllUsers() {
        var resp = userCommandStub.restoreAllUser(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllUsersPermanent() {
        var resp = userCommandStub.deleteAllUserPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static int nvl(Long value) {
        return value == null ? 0 : value.intValue();
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
