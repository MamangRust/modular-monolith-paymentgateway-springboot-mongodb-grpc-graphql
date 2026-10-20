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
import com.sanedge.paymentgateway.gateway.dto.inputs.CardNumberYearIntInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.CardNumberYearMonthIntInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateTransferInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateTransferInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearMonthIntInput;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.TransferResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.amount.TransferYearAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseMonthStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseYearStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseYearStatusSuccess;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.transfer.TransferCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.TransferQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferStatsStatusServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TransferResolver {

    private final TransferQueryServiceGrpc.TransferQueryServiceBlockingStub transferQueryStub;
    private final TransferCommandServiceGrpc.TransferCommandServiceBlockingStub transferCommandStub;
    private final TransferStatsAmountServiceGrpc.TransferStatsAmountServiceBlockingStub transferStatsAmountStub;
    private final TransferStatsStatusServiceGrpc.TransferStatsStatusServiceBlockingStub transferStatsStatusStub;

    // ---- CRUD ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransferResponse>> transfers(@Argument PageInput input) {
        var resp = transferQueryStub.findAllTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindAllTransferRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransferResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfers(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<TransferResponse> transfer(@Argument Long id) {
        var resp = transferQueryStub.findByIdTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(nvl(id))
                        .build());
        return ApiResponse.<TransferResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfer(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransferResponseDeleteAt>> activeTransfers(@Argument PageInput input) {
        var resp = transferQueryStub.findByActiveTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindAllTransferRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransferResponseDeleteAt>> trashedTransfers(@Argument PageInput input) {
        var resp = transferQueryStub.findByTrashedTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindAllTransferRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ApiResponse<List<TransferResponse>> transfersByFrom(@Argument String transferFrom) {
        var resp = transferQueryStub.findTransferByTransferFrom(
                com.sanedge.paymentgateway.pb.transfer.FindTransferByTransferFromRequest.newBuilder()
                        .setTransferFrom(nvl(transferFrom))
                        .build());
        return ApiResponse.<List<TransferResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfers(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ApiResponse<List<TransferResponse>> transfersByTo(@Argument String transferTo) {
        var resp = transferQueryStub.findTransferByTransferTo(
                com.sanedge.paymentgateway.pb.transfer.FindTransferByTransferToRequest.newBuilder()
                        .setTransferTo(nvl(transferTo))
                        .build());
        return ApiResponse.<List<TransferResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfers(resp.getDataList()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ApiResponse<TransferResponse> createTransfer(@Argument CreateTransferInput input) {
        var resp = transferCommandStub.createTransfer(
                com.sanedge.paymentgateway.pb.transfer.CreateTransferRequest.newBuilder()
                        .setTransferFrom(nvl(input != null ? input.transferFrom() : null))
                        .setTransferTo(nvl(input != null ? input.transferTo() : null))
                        .setTransferAmount(nvl(input != null ? input.transferAmount() : null))
                        .build());
        return ApiResponse.<TransferResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfer(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<TransferResponse> updateTransfer(@Argument UpdateTransferInput input) {
        var resp = transferCommandStub.updateTransfer(
                com.sanedge.paymentgateway.pb.transfer.UpdateTransferRequest.newBuilder()
                        .setTransferId(nvl(input != null ? input.transferId() : null))
                        .setTransferFrom(nvl(input != null ? input.transferFrom() : null))
                        .setTransferTo(nvl(input != null ? input.transferTo() : null))
                        .setTransferAmount(nvl(input != null ? input.transferAmount() : null))
                        .build());
        return ApiResponse.<TransferResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfer(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<TransferResponseDeleteAt> trashTransfer(@Argument Long id) {
        var resp = transferCommandStub.trashedTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(nvl(id))
                        .build());
        return ApiResponse.<TransferResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<TransferResponseDeleteAt> restoreTransfer(@Argument Long id) {
        var resp = transferCommandStub.restoreTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(nvl(id))
                        .build());
        return ApiResponse.<TransferResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Boolean> deleteTransferPermanent(@Argument Long id) {
        var resp = transferCommandStub.deleteTransferPermanent(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(nvl(id))
                        .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllTransfers() {
        var resp = transferCommandStub.restoreAllTransfer(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllTransfersPermanent() {
        var resp = transferCommandStub.deleteAllTransferPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    // ---- stats: amount ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferMonthAmountResponse>> transferMonthlyAmount(@Argument YearInput input) {
        var resp = transferStatsAmountStub
                .findMonthlyTransferAmounts(com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferYearAmountResponse>> transferYearlyAmount(@Argument YearInput input) {
        var resp = transferStatsAmountStub
                .findYearlyTransferAmounts(com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferYearAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferMonthAmountResponse>> transferMonthlyAmountBySender(
            @Argument CardNumberYearIntInput input) {
        var resp = transferStatsAmountStub.findMonthlyTransferAmountsBySenderCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindByCardNumberTransferRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferMonthAmountResponse>> transferMonthlyAmountByReceiver(
            @Argument CardNumberYearIntInput input) {
        var resp = transferStatsAmountStub.findMonthlyTransferAmountsByReceiverCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindByCardNumberTransferRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferYearAmountResponse>> transferYearlyAmountBySender(
            @Argument CardNumberYearIntInput input) {
        var resp = transferStatsAmountStub.findYearlyTransferAmountsBySenderCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindByCardNumberTransferRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferYearAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferYearAmountResponse>> transferYearlyAmountByReceiver(
            @Argument CardNumberYearIntInput input) {
        var resp = transferStatsAmountStub.findYearlyTransferAmountsByReceiverCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindByCardNumberTransferRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferYearAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: status ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseMonthStatusSuccess>> transferMonthlyStatusSuccess(
            @Argument YearMonthIntInput input) {
        var resp = transferStatsStatusStub.findMonthlyTransferStatusSuccess(
                com.sanedge.paymentgateway.pb.transfer.FindMonthlyTransferStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransferResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseYearStatusSuccess>> transferYearlyStatusSuccess(
            @Argument YearInput input) {
        var resp = transferStatsStatusStub
                .findYearlyTransferStatusSuccess(com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseMonthStatusFailed>> transferMonthlyStatusFailed(
            @Argument YearMonthIntInput input) {
        var resp = transferStatsStatusStub.findMonthlyTransferStatusFailed(
                com.sanedge.paymentgateway.pb.transfer.FindMonthlyTransferStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransferResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseYearStatusFailed>> transferYearlyStatusFailed(
            @Argument YearInput input) {
        var resp = transferStatsStatusStub
                .findYearlyTransferStatusFailed(com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseMonthStatusSuccess>> transferMonthlyStatusSuccessByCard(
            @Argument CardNumberYearMonthIntInput input) {
        var resp = transferStatsStatusStub.findMonthlyTransferStatusSuccessByCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindMonthlyTransferStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransferResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseYearStatusSuccess>> transferYearlyStatusSuccessByCard(
            @Argument CardNumberYearIntInput input) {
        var resp = transferStatsStatusStub.findYearlyTransferStatusSuccessByCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseMonthStatusFailed>> transferMonthlyStatusFailedByCard(
            @Argument CardNumberYearMonthIntInput input) {
        var resp = transferStatsStatusStub.findMonthlyTransferStatusFailedByCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindMonthlyTransferStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransferResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransferResponseYearStatusFailed>> transferYearlyStatusFailedByCard(
            @Argument CardNumberYearIntInput input) {
        var resp = transferStatsStatusStub.findYearlyTransferStatusFailedByCardNumber(
                com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransferResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferYearStatusFailures(resp.getDataList()))
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
