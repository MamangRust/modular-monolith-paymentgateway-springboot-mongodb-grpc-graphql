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
import com.sanedge.paymentgateway.gateway.dto.inputs.CardNumberYearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.CardNumberYearMonthInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateTopupInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.TopupsByCardInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateTopupInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearMonthFilterInput;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.TopupResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.TopupResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.amount.TopupYearlyAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.method.TopupMonthMethodResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.method.TopupYearlyMethodResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.stats.status.TopupResponseYearStatusSuccess;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.topup.TopupCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.TopupQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsStatusServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TopupResolver {

    private final TopupQueryServiceGrpc.TopupQueryServiceBlockingStub topupQueryStub;
    private final TopupCommandServiceGrpc.TopupCommandServiceBlockingStub topupCommandStub;
    private final TopupStatsAmountServiceGrpc.TopupStatsAmountServiceBlockingStub topupStatsAmountStub;
    private final TopupStatsMethodServiceGrpc.TopupStatsMethodServiceBlockingStub topupStatsMethodStub;
    private final TopupStatsStatusServiceGrpc.TopupStatsStatusServiceBlockingStub topupStatsStatusStub;

    // ---- CRUD ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TopupResponse>> topups(@Argument PageInput input) {
        var resp = topupQueryStub.findAllTopup(com.sanedge.paymentgateway.pb.topup.FindAllTopupRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<TopupResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topups(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TopupResponse>> topupsByCard(@Argument TopupsByCardInput input) {
        var resp = topupQueryStub.findAllTopupByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindAllTopupByCardNumberRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TopupResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topups(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TopupResponseDeleteAt>> activeTopups(@Argument PageInput input) {
        var resp = topupQueryStub.findByActive(com.sanedge.paymentgateway.pb.topup.FindAllTopupRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TopupResponseDeleteAt>> trashedTopups(@Argument PageInput input) {
        var resp = topupQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.topup.FindAllTopupRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ApiResponse<TopupResponse> topup(@Argument Long id) {
        var resp = topupQueryStub.findByIdTopup(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(nvl(id))
                .build());
        return ApiResponse.<TopupResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topup(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<List<TopupResponse>> topupByCard(@Argument String cardNumber) {
        var resp = topupQueryStub.findByCardNumberTopup(
                com.sanedge.paymentgateway.pb.topup.FindByCardNumberTopupRequest.newBuilder()
                        .setCardNumber(nvl(cardNumber))
                        .build());
        return ApiResponse.<List<TopupResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(resp.hasData() ? List.of(ProtoMapper.topup(resp.getData())) : List.of())
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<TopupResponse> createTopup(@Argument CreateTopupInput input) {
        var resp = topupCommandStub.createTopup(com.sanedge.paymentgateway.pb.topup.CreateTopupRequest.newBuilder()
                .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                .setTopupNo("")
                .setTopupAmount(nvl(input != null ? input.topupAmount() : null))
                .setTopupMethod(nvl(input != null ? input.topupMethod() : null))
                .build());
        return ApiResponse.<TopupResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topup(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<TopupResponse> updateTopup(@Argument UpdateTopupInput input) {
        var resp = topupCommandStub.updateTopup(com.sanedge.paymentgateway.pb.topup.UpdateTopupRequest.newBuilder()
                .setTopupId(nvl(input != null ? input.topupId() : null))
                .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                .setTopupAmount(nvl(input != null ? input.topupAmount() : null))
                .setTopupMethod(nvl(input != null ? input.topupMethod() : null))
                .build());
        return ApiResponse.<TopupResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topup(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<TopupResponseDeleteAt> trashTopup(@Argument Long id) {
        var resp = topupCommandStub.trashedTopup(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(nvl(id))
                .build());
        return ApiResponse.<TopupResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<TopupResponseDeleteAt> restoreTopup(@Argument Long id) {
        var resp = topupCommandStub.restoreTopup(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(nvl(id))
                .build());
        return ApiResponse.<TopupResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteTopupPermanent(@Argument Long id) {
        var resp = topupCommandStub.deleteTopupPermanent(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(nvl(id))
                .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllTopups() {
        var resp = topupCommandStub.restoreAllTopup(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllTopupsPermanent() {
        var resp = topupCommandStub.deleteAllTopupPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    // ---- stats: amount ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupMonthAmountResponse>> topupMonthlyAmount(@Argument YearInput input) {
        var resp = topupStatsAmountStub
                .findMonthlyTopupAmounts(com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupYearlyAmountResponse>> topupYearlyAmount(@Argument YearInput input) {
        var resp = topupStatsAmountStub
                .findYearlyTopupAmounts(com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupMonthAmountResponse>> topupMonthlyAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = topupStatsAmountStub.findMonthlyTopupAmountsByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindYearTopupCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupYearlyAmountResponse>> topupYearlyAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = topupStatsAmountStub.findYearlyTopupAmountsByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindYearTopupCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: method ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupMonthMethodResponse>> topupMonthlyMethod(@Argument YearInput input) {
        var resp = topupStatsMethodStub
                .findMonthlyTopupMethods(com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupMonthMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupYearlyMethodResponse>> topupYearlyMethod(@Argument YearInput input) {
        var resp = topupStatsMethodStub
                .findYearlyTopupMethods(com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupYearlyMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupMonthMethodResponse>> topupMonthlyMethodByCard(
            @Argument CardNumberYearInput input) {
        var resp = topupStatsMethodStub.findMonthlyTopupMethodsByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindYearTopupCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupMonthMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupYearlyMethodResponse>> topupYearlyMethodByCard(
            @Argument CardNumberYearInput input) {
        var resp = topupStatsMethodStub.findYearlyTopupMethodsByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindYearTopupCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupYearlyMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearMethods(resp.getDataList()))
                .build();
    }

    // ---- stats: status ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseMonthStatusSuccess>> topupMonthlyStatusSuccess(
            @Argument YearMonthFilterInput input) {
        var resp = topupStatsStatusStub.findMonthlyTopupStatusSuccess(
                com.sanedge.paymentgateway.pb.topup.FindMonthlyTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseYearStatusSuccess>> topupYearlyStatusSuccess(@Argument YearInput input) {
        var resp = topupStatsStatusStub
                .findYearlyTopupStatusSuccess(com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseMonthStatusFailed>> topupMonthlyStatusFailed(
            @Argument YearMonthFilterInput input) {
        var resp = topupStatsStatusStub.findMonthlyTopupStatusFailed(
                com.sanedge.paymentgateway.pb.topup.FindMonthlyTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseYearStatusFailed>> topupYearlyStatusFailed(@Argument YearInput input) {
        var resp = topupStatsStatusStub
                .findYearlyTopupStatusFailed(com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseMonthStatusSuccess>> topupMonthlyStatusSuccessByCard(
            @Argument CardNumberYearMonthInput input) {
        var resp = topupStatsStatusStub.findMonthlyTopupStatusSuccessByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindMonthlyTopupStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TopupResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseYearStatusSuccess>> topupYearlyStatusSuccessByCard(
            @Argument CardNumberYearInput input) {
        var resp = topupStatsStatusStub.findYearlyTopupStatusSuccessByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindYearTopupStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseMonthStatusFailed>> topupMonthlyStatusFailedByCard(
            @Argument CardNumberYearMonthInput input) {
        var resp = topupStatsStatusStub.findMonthlyTopupStatusFailedByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindMonthlyTopupStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TopupResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<TopupResponseYearStatusFailed>> topupYearlyStatusFailedByCard(
            @Argument CardNumberYearInput input) {
        var resp = topupStatsStatusStub.findYearlyTopupStatusFailedByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindYearTopupStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TopupResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupYearStatusFailures(resp.getDataList()))
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
