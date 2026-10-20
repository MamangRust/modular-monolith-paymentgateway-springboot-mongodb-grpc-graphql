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
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateWithdrawInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateWithdrawInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.WithdrawCardNumberMonthInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearMonthCardNumberIntInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearMonthFilterInput;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.WithdrawResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.withdraw.WithdrawCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.WithdrawQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawStatsStatusServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WithdrawResolver {

    private final WithdrawQueryServiceGrpc.WithdrawQueryServiceBlockingStub withdrawQueryStub;
    private final WithdrawCommandServiceGrpc.WithdrawCommandServiceBlockingStub withdrawCommandStub;
    private final WithdrawStatsAmountServiceGrpc.WithdrawStatsAmountServiceBlockingStub withdrawStatsAmountStub;
    private final WithdrawStatsStatusServiceGrpc.WithdrawStatsStatusServiceBlockingStub withdrawStatsStatusStub;

    // ---- CRUD ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<WithdrawResponse>> withdraws(@Argument PageInput input) {
        var resp = withdrawQueryStub.findAllWithdraw(
                com.sanedge.paymentgateway.pb.withdraw.FindAllWithdrawRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<WithdrawResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdraws(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<WithdrawResponse> withdraw(@Argument Long id) {
        var resp = withdrawQueryStub.findByIdWithdraw(
                com.sanedge.paymentgateway.pb.withdraw.FindByIdWithdrawRequest.newBuilder()
                        .setWithdrawId(nvl(id))
                        .build());
        return ApiResponse.<WithdrawResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdraw(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<WithdrawResponse>> withdrawsByCard(@Argument String cardNumber) {
        var resp = withdrawQueryStub.findByCardNumber(
                com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                        .setCardNumber(nvl(cardNumber))
                        .build());
        return ApiResponse.<List<WithdrawResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdraws(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<WithdrawResponseDeleteAt>> activeWithdraws(@Argument PageInput input) {
        var resp = withdrawQueryStub.findByActive(
                com.sanedge.paymentgateway.pb.withdraw.FindAllWithdrawRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<WithdrawResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<WithdrawResponseDeleteAt>> trashedWithdraws(@Argument PageInput input) {
        var resp = withdrawQueryStub.findByTrashed(
                com.sanedge.paymentgateway.pb.withdraw.FindAllWithdrawRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<WithdrawResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<WithdrawResponse> createWithdraw(@Argument CreateWithdrawInput input) {
        var resp = withdrawCommandStub.createWithdraw(
                com.sanedge.paymentgateway.pb.withdraw.CreateWithdrawRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setWithdrawAmount(nvl(input != null ? input.withdrawAmount() : null))
                        .setWithdrawTime(toTimestamp(input != null ? input.withdrawTime() : null))
                        .build());
        return ApiResponse.<WithdrawResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdraw(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<WithdrawResponse> updateWithdraw(@Argument UpdateWithdrawInput input) {
        var resp = withdrawCommandStub.updateWithdraw(
                com.sanedge.paymentgateway.pb.withdraw.UpdateWithdrawRequest.newBuilder()
                        .setWithdrawId(nvl(input != null ? input.withdrawId() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setWithdrawAmount(nvl(input != null ? input.withdrawAmount() : null))
                        .setWithdrawTime(toTimestamp(input != null ? input.withdrawTime() : null))
                        .build());
        return ApiResponse.<WithdrawResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdraw(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<WithdrawResponseDeleteAt> trashWithdraw(@Argument Long id) {
        var resp = withdrawCommandStub.trashedWithdraw(
                com.sanedge.paymentgateway.pb.withdraw.FindByIdWithdrawRequest.newBuilder()
                        .setWithdrawId(nvl(id))
                        .build());
        return ApiResponse.<WithdrawResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<WithdrawResponseDeleteAt> restoreWithdraw(@Argument Long id) {
        var resp = withdrawCommandStub.restoreWithdraw(
                com.sanedge.paymentgateway.pb.withdraw.FindByIdWithdrawRequest.newBuilder()
                        .setWithdrawId(nvl(id))
                        .build());
        return ApiResponse.<WithdrawResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteWithdrawPermanent(@Argument Long id) {
        var resp = withdrawCommandStub.deleteWithdrawPermanent(
                com.sanedge.paymentgateway.pb.withdraw.FindByIdWithdrawRequest.newBuilder()
                        .setWithdrawId(nvl(id))
                        .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllWithdraws() {
        var resp = withdrawCommandStub.restoreAllWithdraw(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllWithdrawsPermanent() {
        var resp = withdrawCommandStub.deleteAllWithdrawPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    // ---- stats: amount ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawMonthlyAmountResponse>> withdrawMonthlyAmount(@Argument YearInput input) {
        var resp = withdrawStatsAmountStub
                .findMonthlyWithdraws(com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawYearlyAmountResponse>> withdrawYearlyAmount(@Argument YearInput input) {
        var resp = withdrawStatsAmountStub
                .findYearlyWithdraws(com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawMonthlyAmountResponse>> withdrawMonthlyAmountByCard(
            @Argument YearMonthCardNumberIntInput input) {
        var resp = withdrawStatsAmountStub.findMonthlyWithdrawsByCardNumber(
                com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawYearlyAmountResponse>> withdrawYearlyAmountByCard(
            @Argument YearMonthCardNumberIntInput input) {
        var resp = withdrawStatsAmountStub.findYearlyWithdrawsByCardNumber(
                com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: status ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseMonthStatusSuccess>> withdrawMonthlyStatusSuccess(
            @Argument YearMonthFilterInput input) {
        var resp = withdrawStatsStatusStub.findMonthlyWithdrawStatusSuccess(
                com.sanedge.paymentgateway.pb.withdraw.FindMonthlyWithdrawStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseYearStatusSuccess>> withdrawYearlyStatusSuccess(
            @Argument YearInput input) {
        var resp = withdrawStatsStatusStub
                .findYearlyWithdrawStatusSuccess(com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseMonthStatusFailed>> withdrawMonthlyStatusFailed(
            @Argument YearMonthFilterInput input) {
        var resp = withdrawStatsStatusStub.findMonthlyWithdrawStatusFailed(
                com.sanedge.paymentgateway.pb.withdraw.FindMonthlyWithdrawStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseYearStatusFailed>> withdrawYearlyStatusFailed(
            @Argument YearInput input) {
        var resp = withdrawStatsStatusStub
                .findYearlyWithdrawStatusFailed(com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawYearStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseMonthStatusSuccess>> withdrawMonthlyStatusSuccessByCard(
            @Argument WithdrawCardNumberMonthInput input) {
        var resp = withdrawStatsStatusStub.findMonthlyWithdrawStatusSuccessCardNumber(
                com.sanedge.paymentgateway.pb.withdraw.FindMonthlyWithdrawStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseYearStatusSuccess>> withdrawYearlyStatusSuccessByCard(
            @Argument YearMonthCardNumberIntInput input) {
        var resp = withdrawStatsStatusStub.findYearlyWithdrawStatusSuccessCardNumber(
                com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseMonthStatusFailed>> withdrawMonthlyStatusFailedByCard(
            @Argument WithdrawCardNumberMonthInput input) {
        var resp = withdrawStatsStatusStub.findMonthlyWithdrawStatusFailedCardNumber(
                com.sanedge.paymentgateway.pb.withdraw.FindMonthlyWithdrawStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<List<WithdrawResponseYearStatusFailed>> withdrawYearlyStatusFailedByCard(
            @Argument YearMonthCardNumberIntInput input) {
        var resp = withdrawStatsStatusStub.findYearlyWithdrawStatusFailedCardNumber(
                com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<WithdrawResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.withdrawYearStatusFailures(resp.getDataList()))
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

    private static com.google.protobuf.Timestamp toTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return com.google.protobuf.Timestamp.getDefaultInstance();
        }
        return ProtoMapper.toTimestamp(java.time.LocalDateTime.parse(value));
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
