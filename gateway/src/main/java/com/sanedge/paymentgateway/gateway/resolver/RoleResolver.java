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
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateRoleInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateRoleInput;
import com.sanedge.paymentgateway.gateway.dto.responses.role.RoleResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.role.RoleResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.role.RoleCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.role.RoleServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RoleResolver {

    private final RoleServiceGrpc.RoleServiceBlockingStub roleQueryStub;
    private final RoleCommandServiceGrpc.RoleCommandServiceBlockingStub roleCommandStub;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<RoleResponse>> roles(@Argument PageInput input) {
        var resp = roleQueryStub.findAllRole(com.sanedge.paymentgateway.pb.role.FindAllRoleRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<RoleResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roles(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<RoleResponseDeleteAt>> activeRoles(@Argument PageInput input) {
        var resp = roleQueryStub.findByActive(com.sanedge.paymentgateway.pb.role.FindAllRoleRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.rolesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<RoleResponseDeleteAt>> trashedRoles(@Argument PageInput input) {
        var resp = roleQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.role.FindAllRoleRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.rolesDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<RoleResponse> role(@Argument Long id) {
        var resp = roleQueryStub.findByIdRole(com.sanedge.paymentgateway.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(nvl(id))
                .build());
        return ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<RoleResponse>> rolesByUser(@Argument Long userId) {
        var resp = roleQueryStub.findByUserId(com.sanedge.paymentgateway.pb.role.FindByIdUserRoleRequest.newBuilder()
                .setUserId(nvl(userId))
                .build());
        return ApiResponse.<List<RoleResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roles(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<RoleResponse> roleByName(@Argument String name) {
        var resp = roleQueryStub.findByNameRole(com.sanedge.paymentgateway.pb.role.FindByNameRoleRequest.newBuilder()
                .setName(nvl(name))
                .build());
        return ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoleResponse> createRole(@Argument CreateRoleInput input) {
        var resp = roleCommandStub.createRole(com.sanedge.paymentgateway.pb.role.CreateRoleRequest.newBuilder()
                .setName(nvl(input.name()))
                .build());
        return ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoleResponse> updateRole(@Argument UpdateRoleInput input) {
        var resp = roleCommandStub.updateRole(com.sanedge.paymentgateway.pb.role.UpdateRoleRequest.newBuilder()
                .setId(nvl(input.id()))
                .setName(nvl(input.name()))
                .build());
        return ApiResponse.<RoleResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.role(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoleResponseDeleteAt> trashRole(@Argument Long id) {
        var resp = roleCommandStub.trashedRole(com.sanedge.paymentgateway.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(nvl(id))
                .build());
        return ApiResponse.<RoleResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roleDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<RoleResponseDeleteAt> restoreRole(@Argument Long id) {
        var resp = roleCommandStub.restoreRole(com.sanedge.paymentgateway.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(nvl(id))
                .build());
        return ApiResponse.<RoleResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.roleDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteRolePermanent(@Argument Long id) {
        var resp = roleCommandStub.deleteRolePermanent(com.sanedge.paymentgateway.pb.role.FindByIdRoleRequest.newBuilder()
                .setRoleId(nvl(id))
                .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllRoles() {
        var resp = roleCommandStub.restoreAllRole(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllRolesPermanent() {
        var resp = roleCommandStub.deleteAllRolePermanent(Empty.getDefaultInstance());
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
