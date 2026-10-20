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
import com.sanedge.paymentgateway.gateway.dto.inputs.ApiKeyYearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateMerchantInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.MerchantIdYearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.MerchantTransactionsByApiKeyInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.MerchantTransactionsByIdInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateMerchantInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.MerchantResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.MerchantTransactionResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.amount.MerchantResponseYearlyAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.merchant.MerchantCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.MerchantTransactionServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsTotalAmountServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MerchantResolver {

    private final MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub;
    private final MerchantCommandServiceGrpc.MerchantCommandServiceBlockingStub merchantCommandStub;
    private final MerchantTransactionServiceGrpc.MerchantTransactionServiceBlockingStub merchantTransactionStub;
    private final MerchantStatsAmountServiceGrpc.MerchantStatsAmountServiceBlockingStub merchantStatsAmountStub;
    private final MerchantStatsMethodServiceGrpc.MerchantStatsMethodServiceBlockingStub merchantStatsMethodStub;
    private final MerchantStatsTotalAmountServiceGrpc.MerchantStatsTotalAmountServiceBlockingStub merchantStatsTotalAmountStub;

    // ---- CRUD ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<MerchantResponse>> merchants(@Argument PageInput input) {
        var resp = merchantQueryStub.findAllMerchant(com.sanedge.paymentgateway.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<MerchantResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchants(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<MerchantResponseDeleteAt>> activeMerchants(@Argument PageInput input) {
        var resp = merchantQueryStub.findByActive(com.sanedge.paymentgateway.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<MerchantResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<MerchantResponseDeleteAt>> trashedMerchants(@Argument PageInput input) {
        var resp = merchantQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.merchant.FindAllMerchantRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<MerchantResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<MerchantResponse> merchant(@Argument Long id) {
        var resp = merchantQueryStub.findByIdMerchant(com.sanedge.paymentgateway.pb.merchant.FindByIdMerchantRequest.newBuilder()
                .setMerchantId(nvl(id))
                .build());
        return ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<MerchantResponse> merchantByApiKey(@Argument String apiKey) {
        var resp = merchantQueryStub.findByApiKey(com.sanedge.paymentgateway.pb.merchant.FindByApiKeyRequest.newBuilder()
                .setApiKey(nvl(apiKey))
                .build());
        return ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<MerchantResponse>> merchantByUserId(@Argument Long userId) {
        var resp = merchantQueryStub.findByMerchantUserId(
                com.sanedge.paymentgateway.pb.merchant.FindByMerchantUserIdRequest.newBuilder()
                        .setUserId(nvl(userId))
                        .build());
        return ApiResponse.<List<MerchantResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchants(resp.getDataList()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<MerchantResponse> createMerchant(@Argument CreateMerchantInput input) {
        var resp = merchantCommandStub.createMerchant(com.sanedge.paymentgateway.pb.merchant.CreateMerchantRequest.newBuilder()
                .setName(nvl(input != null ? input.name() : null))
                .setUserId(nvl(input != null ? input.userId() : null))
                .build());
        return ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<MerchantResponse> updateMerchant(@Argument UpdateMerchantInput input) {
        var resp = merchantCommandStub.updateMerchant(com.sanedge.paymentgateway.pb.merchant.UpdateMerchantRequest.newBuilder()
                .setMerchantId(nvl(input != null ? input.merchantId() : null))
                .setName(nvl(input != null ? input.name() : null))
                .setUserId(nvl(input != null ? input.userId() : null))
                .setStatus(nvl(input != null ? input.status() : null))
                .build());
        return ApiResponse.<MerchantResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchant(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<MerchantResponseDeleteAt> trashMerchant(@Argument Long id) {
        var resp = merchantCommandStub.trashedMerchant(
                com.sanedge.paymentgateway.pb.merchant.FindByIdMerchantRequest.newBuilder()
                        .setMerchantId(nvl(id))
                        .build());
        return ApiResponse.<MerchantResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<MerchantResponseDeleteAt> restoreMerchant(@Argument Long id) {
        var resp = merchantCommandStub.restoreMerchant(
                com.sanedge.paymentgateway.pb.merchant.FindByIdMerchantRequest.newBuilder()
                        .setMerchantId(nvl(id))
                        .build());
        return ApiResponse.<MerchantResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteMerchantPermanent(@Argument Long id) {
        var resp = merchantCommandStub.deleteMerchantPermanent(
                com.sanedge.paymentgateway.pb.merchant.FindByIdMerchantRequest.newBuilder()
                        .setMerchantId(nvl(id))
                        .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllMerchants() {
        var resp = merchantCommandStub.restoreAllMerchant(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllMerchantsPermanent() {
        var resp = merchantCommandStub.deleteAllMerchantPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    // ---- transactions ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<MerchantTransactionResponse>> merchantTransactions(@Argument PageInput input) {
        var resp = merchantTransactionStub.findAllTransactionMerchant(
                com.sanedge.paymentgateway.pb.merchant.FindAllMerchantTransaction.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantTransactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<MerchantTransactionResponse>> merchantTransactionsById(
            @Argument MerchantTransactionsByIdInput input) {
        var resp = merchantTransactionStub.findAllTransactionByMerchant(
                com.sanedge.paymentgateway.pb.merchant.FindAllMerchantTransactionId.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .setId(input == null || input.merchantId() == null ? "" : String.valueOf(input.merchantId()))
                        .build());
        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantTransactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<MerchantTransactionResponse>> merchantTransactionsByApiKey(
            @Argument MerchantTransactionsByApiKeyInput input) {
        var resp = merchantTransactionStub.findAllTransactionByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindAllMerchantTransactionApikey.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantTransactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    // ---- stats: amount ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyAmount>> merchantMonthlyAmount(@Argument YearInput input) {
        var resp = merchantStatsAmountStub
                .findMonthlyAmountMerchant(com.sanedge.paymentgateway.pb.merchant.FindYearMerchant.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyAmount>> merchantYearlyAmount(@Argument YearInput input) {
        var resp = merchantStatsAmountStub
                .findYearlyAmountMerchant(com.sanedge.paymentgateway.pb.merchant.FindYearMerchant.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyAmount>> merchantMonthlyAmountById(
            @Argument MerchantIdYearInput input) {
        var resp = merchantStatsAmountStub.findMonthlyAmountByMerchants(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyAmount>> merchantYearlyAmountById(
            @Argument MerchantIdYearInput input) {
        var resp = merchantStatsAmountStub.findYearlyAmountByMerchants(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyAmount>> merchantMonthlyAmountByApiKey(
            @Argument ApiKeyYearInput input) {
        var resp = merchantStatsAmountStub.findMonthlyAmountByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyAmount>> merchantYearlyAmountByApiKey(
            @Argument ApiKeyYearInput input) {
        var resp = merchantStatsAmountStub.findYearlyAmountByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: method ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> merchantMonthlyMethod(@Argument YearInput input) {
        var resp = merchantStatsMethodStub
                .findMonthlyPaymentMethodsMerchant(com.sanedge.paymentgateway.pb.merchant.FindYearMerchant.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyPaymentMethod>> merchantYearlyMethod(@Argument YearInput input) {
        var resp = merchantStatsMethodStub
                .findYearlyPaymentMethodMerchant(com.sanedge.paymentgateway.pb.merchant.FindYearMerchant.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> merchantMonthlyMethodById(
            @Argument MerchantIdYearInput input) {
        var resp = merchantStatsMethodStub.findMonthlyPaymentMethodByMerchants(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyPaymentMethod>> merchantYearlyMethodById(
            @Argument MerchantIdYearInput input) {
        var resp = merchantStatsMethodStub.findYearlyPaymentMethodByMerchants(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> merchantMonthlyMethodByApiKey(
            @Argument ApiKeyYearInput input) {
        var resp = merchantStatsMethodStub.findMonthlyPaymentMethodByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyPaymentMethod>> merchantYearlyMethodByApiKey(
            @Argument ApiKeyYearInput input) {
        var resp = merchantStatsMethodStub.findYearlyPaymentMethodByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearMethods(resp.getDataList()))
                .build();
    }

    // ---- stats: total amount ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> merchantMonthlyTotalAmount(
            @Argument YearInput input) {
        var resp = merchantStatsTotalAmountStub
                .findMonthlyTotalAmountMerchant(com.sanedge.paymentgateway.pb.merchant.FindYearMerchant.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthTotalAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyTotalAmount>> merchantYearlyTotalAmount(
            @Argument YearInput input) {
        var resp = merchantStatsTotalAmountStub
                .findYearlyTotalAmountMerchant(com.sanedge.paymentgateway.pb.merchant.FindYearMerchant.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearTotalAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> merchantMonthlyTotalAmountById(
            @Argument MerchantIdYearInput input) {
        var resp = merchantStatsTotalAmountStub.findMonthlyTotalAmountByMerchants(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthTotalAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyTotalAmount>> merchantYearlyTotalAmountById(
            @Argument MerchantIdYearInput input) {
        var resp = merchantStatsTotalAmountStub.findYearlyTotalAmountByMerchants(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearTotalAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseMonthlyTotalAmount>> merchantMonthlyTotalAmountByApiKey(
            @Argument ApiKeyYearInput input) {
        var resp = merchantStatsTotalAmountStub.findMonthlyTotalAmountByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseMonthlyTotalAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantMonthTotalAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<MerchantResponseYearlyTotalAmount>> merchantYearlyTotalAmountByApiKey(
            @Argument ApiKeyYearInput input) {
        var resp = merchantStatsTotalAmountStub.findYearlyTotalAmountByApikey(
                com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setApiKey(nvl(input != null ? input.apiKey() : null))
                        .build());
        return ApiResponse.<List<MerchantResponseYearlyTotalAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.merchantYearTotalAmounts(resp.getDataList()))
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
