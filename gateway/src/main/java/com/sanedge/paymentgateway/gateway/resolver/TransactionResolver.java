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
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateTransactionInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.TransactionsByCardInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateTransactionInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearMonthFilterInput;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.TransactionResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.amount.TransactionYearlyAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.method.TransactionYearMethodResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.status.TransactionResponseMonthStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.status.TransactionResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.status.TransactionResponseYearStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.stats.status.TransactionResponseYearStatusSuccess;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.transaction.TransactionCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.TransactionQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsStatusServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TransactionResolver {

    private final TransactionQueryServiceGrpc.TransactionQueryServiceBlockingStub transactionQueryStub;
    private final TransactionCommandServiceGrpc.TransactionCommandServiceBlockingStub transactionCommandStub;
    private final TransactionStatsAmountServiceGrpc.TransactionStatsAmountServiceBlockingStub transactionStatsAmountStub;
    private final TransactionStatsMethodServiceGrpc.TransactionStatsMethodServiceBlockingStub transactionStatsMethodStub;
    private final TransactionStatsStatusServiceGrpc.TransactionStatsStatusServiceBlockingStub transactionStatsStatusStub;

    // ---- CRUD ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransactionResponse>> transactions(@Argument PageInput input) {
        var resp = transactionQueryStub.findAllTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransactionResponse>> transactionsByCard(
            @Argument TransactionsByCardInput input) {
        var resp = transactionQueryStub.findAllTransactionByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionCardNumberRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransactionResponseDeleteAt>> activeTransactions(@Argument PageInput input) {
        var resp = transactionQueryStub.findByActiveTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponsePagination<List<TransactionResponseDeleteAt>> trashedTransactions(@Argument PageInput input) {
        var resp = transactionQueryStub.findByTrashedTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(input != null ? input.page() : null))
                        .setPageSize(nvl(input != null ? input.pageSize() : null))
                        .setSearch(nvl(input != null ? input.search() : null))
                        .build());
        return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<TransactionResponse> transaction(@Argument Long id) {
        var resp = transactionQueryStub.findByIdTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(nvl(id))
                        .build());
        return ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<List<TransactionResponse>> transactionsByMerchantId(@Argument Long merchantId) {
        var resp = transactionQueryStub.findTransactionByMerchantId(
                com.sanedge.paymentgateway.pb.transaction.FindTransactionByMerchantIdRequest.newBuilder()
                        .setMerchantId(nvl(merchantId))
                        .build());
        return ApiResponse.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<TransactionResponse> createTransaction(@Argument String apiKey,
            @Argument CreateTransactionInput input) {
        var resp = transactionCommandStub.createTransaction(
                com.sanedge.paymentgateway.pb.transaction.CreateTransactionRequest.newBuilder()
                        .setApiKey(nvl(apiKey))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setAmount(nvl(input != null ? input.amount() : null))
                        .setPaymentMethod(nvl(input != null ? input.paymentMethod() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .setTransactionTime(toTimestamp(input != null ? input.transactionTime() : null))
                        .build());
        return ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ApiResponse<TransactionResponse> updateTransaction(@Argument String apiKey,
            @Argument UpdateTransactionInput input) {
        var resp = transactionCommandStub.updateTransaction(
                com.sanedge.paymentgateway.pb.transaction.UpdateTransactionRequest.newBuilder()
                        .setTransactionId(nvl(input != null ? input.transactionId() : null))
                        .setApiKey(nvl(apiKey))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setAmount(nvl(input != null ? input.amount() : null))
                        .setPaymentMethod(nvl(input != null ? input.paymentMethod() : null))
                        .setMerchantId(nvl(input != null ? input.merchantId() : null))
                        .setTransactionTime(toTimestamp(input != null ? input.transactionTime() : null))
                        .build());
        return ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<TransactionResponseDeleteAt> trashTransaction(@Argument Long id) {
        var resp = transactionCommandStub.trashedTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(nvl(id))
                        .build());
        return ApiResponse.<TransactionResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<TransactionResponseDeleteAt> restoreTransaction(@Argument Long id) {
        var resp = transactionCommandStub.restoreTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(nvl(id))
                        .build());
        return ApiResponse.<TransactionResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteTransactionPermanent(@Argument Long id) {
        var resp = transactionCommandStub.deleteTransactionPermanent(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(nvl(id))
                        .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllTransactions() {
        var resp = transactionCommandStub.restoreAllTransaction(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllTransactionsPermanent() {
        var resp = transactionCommandStub.deleteAllTransactionPermanent(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    // ---- stats: amount ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionMonthAmountResponse>> transactionMonthlyAmount(@Argument YearInput input) {
        var resp = transactionStatsAmountStub
                .findMonthlyAmounts(com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionYearlyAmountResponse>> transactionYearlyAmount(@Argument YearInput input) {
        var resp = transactionStatsAmountStub
                .findYearlyAmounts(com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionMonthAmountResponse>> transactionMonthlyAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = transactionStatsAmountStub.findMonthlyAmountsByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindByYearCardNumberTransactionRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionYearlyAmountResponse>> transactionYearlyAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = transactionStatsAmountStub.findYearlyAmountsByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindByYearCardNumberTransactionRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: method ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionMonthMethodResponse>> transactionMonthlyMethod(@Argument YearInput input) {
        var resp = transactionStatsMethodStub
                .findMonthlyPaymentMethods(com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionYearMethodResponse>> transactionYearlyMethod(@Argument YearInput input) {
        var resp = transactionStatsMethodStub
                .findYearlyPaymentMethods(com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionMonthMethodResponse>> transactionMonthlyMethodByCard(
            @Argument CardNumberYearInput input) {
        var resp = transactionStatsMethodStub.findMonthlyPaymentMethodsByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindByYearCardNumberTransactionRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthMethods(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionYearMethodResponse>> transactionYearlyMethodByCard(
            @Argument CardNumberYearInput input) {
        var resp = transactionStatsMethodStub.findYearlyPaymentMethodsByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindByYearCardNumberTransactionRequest.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearMethods(resp.getDataList()))
                .build();
    }

    // ---- stats: status ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseMonthStatusSuccess>> transactionMonthlyStatusSuccess(
            @Argument YearMonthFilterInput input) {
        var resp = transactionStatsStatusStub.findMonthlyTransactionStatusSuccess(
                com.sanedge.paymentgateway.pb.transaction.FindMonthlyTransactionStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseYearStatusSuccess>> transactionYearlyStatusSuccess(
            @Argument YearInput input) {
        var resp = transactionStatsStatusStub
                .findYearlyTransactionStatusSuccess(com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus
                        .newBuilder().setYear(nvl(input != null ? input.year() : null)).build());
        return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseMonthStatusFailed>> transactionMonthlyStatusFailed(
            @Argument YearMonthFilterInput input) {
        var resp = transactionStatsStatusStub.findMonthlyTransactionStatusFailed(
                com.sanedge.paymentgateway.pb.transaction.FindMonthlyTransactionStatus.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseYearStatusFailed>> transactionYearlyStatusFailed(
            @Argument YearInput input) {
        var resp = transactionStatsStatusStub
                .findYearlyTransactionStatusFailed(com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus
                        .newBuilder().setYear(nvl(input != null ? input.year() : null)).build());
        return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseMonthStatusSuccess>> transactionMonthlyStatusSuccessByCard(
            @Argument CardNumberYearMonthInput input) {
        var resp = transactionStatsStatusStub.findMonthlyTransactionStatusSuccessByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindMonthlyTransactionStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransactionResponseMonthStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseYearStatusSuccess>> transactionYearlyStatusSuccessByCard(
            @Argument CardNumberYearInput input) {
        var resp = transactionStatsStatusStub.findYearlyTransactionStatusSuccessByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionResponseYearStatusSuccess>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearStatusSuccesses(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseMonthStatusFailed>> transactionMonthlyStatusFailedByCard(
            @Argument CardNumberYearMonthInput input) {
        var resp = transactionStatsStatusStub.findMonthlyTransactionStatusFailedByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindMonthlyTransactionStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .setMonth(nvl(input != null ? input.month() : null))
                        .build());
        return ApiResponse.<List<TransactionResponseMonthStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionMonthStatusFailures(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<TransactionResponseYearStatusFailed>> transactionYearlyStatusFailedByCard(
            @Argument CardNumberYearInput input) {
        var resp = transactionStatsStatusStub.findYearlyTransactionStatusFailedByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatusCardNumber.newBuilder()
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<TransactionResponseYearStatusFailed>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionYearStatusFailures(resp.getDataList()))
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
