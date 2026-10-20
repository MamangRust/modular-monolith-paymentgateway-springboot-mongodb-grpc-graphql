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
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateSaldoInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateSaldoInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearMonthFilterInput;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.SaldoResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.balance.SaldoYearBalanceResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.total_balance.SaldoYearTotalBalanceResponse;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.saldo.SaldoCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.SaldoQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoStatsBalanceServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoStatsTotalBalanceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SaldoResolver {

    private final SaldoQueryServiceGrpc.SaldoQueryServiceBlockingStub saldoQueryStub;
    private final SaldoCommandServiceGrpc.SaldoCommandServiceBlockingStub saldoCommandStub;
    private final SaldoStatsBalanceServiceGrpc.SaldoStatsBalanceServiceBlockingStub saldoStatsBalanceStub;
    private final SaldoStatsTotalBalanceGrpc.SaldoStatsTotalBalanceBlockingStub saldoStatsTotalBalanceStub;

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<SaldoResponse>> saldos(@Argument PageInput input) {
        var resp = saldoQueryStub.findAllSaldo(com.sanedge.paymentgateway.pb.saldo.FindAllSaldoRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<SaldoResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldos(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<SaldoResponseDeleteAt>> activeSaldos(@Argument PageInput input) {
        var resp = saldoQueryStub.findByActive(com.sanedge.paymentgateway.pb.saldo.FindAllSaldoRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<SaldoResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldosDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<SaldoResponseDeleteAt>> trashedSaldos(@Argument PageInput input) {
        var resp = saldoQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.saldo.FindAllSaldoRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<SaldoResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldosDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<SaldoResponse> saldo(@Argument Long id) {
        var resp = saldoQueryStub.findByIdSaldo(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(nvl(id))
                .build());
        return ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<SaldoResponse> saldoByCard(@Argument String cardNumber) {
        var resp = saldoQueryStub.findByCardNumber(com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                .setCardNumber(nvl(cardNumber))
                .build());
        return ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<SaldoResponse> createSaldo(@Argument CreateSaldoInput input) {
        var resp = saldoCommandStub.createSaldo(com.sanedge.paymentgateway.pb.saldo.CreateSaldoRequest.newBuilder()
                .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                .setTotalBalance(nvl(input != null ? input.totalBalance() : null))
                .build());
        return ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<SaldoResponse> updateSaldo(@Argument UpdateSaldoInput input) {
        var resp = saldoCommandStub.updateSaldo(com.sanedge.paymentgateway.pb.saldo.UpdateSaldoRequest.newBuilder()
                .setSaldoId(nvl(input != null ? input.saldoId() : null))
                .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                .setTotalBalance(nvl(input != null ? input.totalBalance() : null))
                .build());
        return ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<SaldoResponseDeleteAt> trashSaldo(@Argument Long id) {
        var resp = saldoCommandStub.trashedSaldo(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(nvl(id))
                .build());
        return ApiResponse.<SaldoResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<SaldoResponseDeleteAt> restoreSaldo(@Argument Long id) {
        var resp = saldoCommandStub.restoreSaldo(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(nvl(id))
                .build());
        return ApiResponse.<SaldoResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteSaldoPermanent(@Argument Long id) {
        var resp = saldoCommandStub.deleteSaldoPermanent(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(nvl(id))
                .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllSaldos() {
        var resp = saldoCommandStub.restoreAllSaldo(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllSaldosPermanent() {
        var resp = saldoCommandStub.deleteAllSaldoPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<SaldoMonthBalanceResponse>> saldoMonthlyBalance(@Argument YearInput input) {
        var resp = saldoStatsBalanceStub
                .findMonthlySaldoBalances(com.sanedge.paymentgateway.pb.saldo.FindYearlySaldo.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<SaldoMonthBalanceResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoMonthBalances(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<SaldoYearBalanceResponse>> saldoYearlyBalance(@Argument YearInput input) {
        var resp = saldoStatsBalanceStub
                .findYearlySaldoBalances(com.sanedge.paymentgateway.pb.saldo.FindYearlySaldo.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<SaldoYearBalanceResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoYearBalances(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<SaldoMonthTotalBalanceResponse>> saldoMonthlyTotalBalance(
            @Argument YearMonthFilterInput input) {
        var resp = saldoStatsTotalBalanceStub.findMonthlyTotalSaldoBalance(
                com.sanedge.paymentgateway.pb.saldo.FindMonthlySaldoTotalBalance.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<SaldoMonthTotalBalanceResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoMonthTotalBalances(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<SaldoYearTotalBalanceResponse>> saldoYearlyTotalBalance(@Argument YearInput input) {
        var resp = saldoStatsTotalBalanceStub
                .findYearTotalSaldoBalance(com.sanedge.paymentgateway.pb.saldo.FindYearlySaldo.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<SaldoYearTotalBalanceResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoYearTotalBalances(resp.getDataList()))
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
