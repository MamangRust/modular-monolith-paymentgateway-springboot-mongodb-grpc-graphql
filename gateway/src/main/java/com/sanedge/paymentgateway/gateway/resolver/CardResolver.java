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
import com.sanedge.paymentgateway.gateway.dto.inputs.CreateCardInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.PageInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.UpdateCardInput;
import com.sanedge.paymentgateway.gateway.dto.inputs.YearInput;
import com.sanedge.paymentgateway.gateway.dto.responses.card.CardResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.card.CardResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.card.dashboard.CardDashboard;
import com.sanedge.paymentgateway.gateway.dto.responses.card.dashboard.CardDashboardCard;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.balance.CardResponseYearBalance;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.card.CardCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardDashboardServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsBalanceServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTopupServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTransactionServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTransferServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsWithdrawServiceGrpc;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CardResolver {

    private final CardQueryServiceGrpc.CardQueryServiceBlockingStub cardQueryStub;
    private final CardCommandServiceGrpc.CardCommandServiceBlockingStub cardCommandStub;
    private final CardStatsBalanceServiceGrpc.CardStatsBalanceServiceBlockingStub cardStatsBalanceStub;
    private final CardStatsTopupServiceGrpc.CardStatsTopupServiceBlockingStub cardStatsTopupStub;
    private final CardStatsTransactionServiceGrpc.CardStatsTransactionServiceBlockingStub cardStatsTransactionStub;
    private final CardStatsTransferServiceGrpc.CardStatsTransferServiceBlockingStub cardStatsTransferStub;
    private final CardStatsWithdrawServiceGrpc.CardStatsWithdrawServiceBlockingStub cardStatsWithdrawStub;
    private final CardDashboardServiceGrpc.CardDashboardServiceBlockingStub cardDashboardStub;

    // ---- CRUD ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<CardResponse>> cards(@Argument PageInput input) {
        var resp = cardQueryStub.findAllCard(com.sanedge.paymentgateway.pb.card.FindAllCardRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<CardResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cards(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponsePagination<List<CardResponseDeleteAt>> activeCards(@Argument PageInput input) {
        var resp = cardQueryStub.findByActiveCard(com.sanedge.paymentgateway.pb.card.FindAllCardRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponsePagination<List<CardResponseDeleteAt>> trashedCards(@Argument PageInput input) {
        var resp = cardQueryStub.findByTrashedCard(com.sanedge.paymentgateway.pb.card.FindAllCardRequest.newBuilder()
                .setPage(nvl(input != null ? input.page() : null))
                .setPageSize(nvl(input != null ? input.pageSize() : null))
                .setSearch(nvl(input != null ? input.search() : null))
                .build());
        return ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<CardResponse> card(@Argument Long id) {
        var resp = cardQueryStub.findByIdCard(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(nvl(id))
                .build());
        return ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<CardResponse> cardByUser(@Argument Long userId) {
        var resp = cardQueryStub.findByUserIdCard(com.sanedge.paymentgateway.pb.card.FindByUserIdCardRequest.newBuilder()
                .setUserId(nvl(userId))
                .build());
        return ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<CardResponse> cardByNumber(@Argument String cardNumber) {
        var resp = cardQueryStub.findByCardNumber(com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                .setCardNumber(nvl(cardNumber))
                .build());
        return ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build();
    }

    // ---- dashboard ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<CardDashboard> cardDashboard() {
        var resp = cardDashboardStub.dashboardCard(Empty.getDefaultInstance());
        return ApiResponse.<CardDashboard>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDashboard(resp.getData()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<CardDashboardCard> cardDashboardByCardNumber(@Argument String cardNumber) {
        var resp = cardDashboardStub.dashboardCardNumber(
                com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                        .setCardNumber(nvl(cardNumber))
                        .build());
        return ApiResponse.<CardDashboardCard>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDashboardCard(resp.getData()))
                .build();
    }

    // ---- stats: balance ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseMonthBalance>> cardMonthlyBalance(@Argument YearInput input) {
        var resp = cardStatsBalanceStub.findMonthlyBalance(
                com.sanedge.paymentgateway.pb.card.stats.FindYearBalance.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthBalance>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthBalances(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseYearBalance>> cardYearlyBalance(@Argument YearInput input) {
        var resp = cardStatsBalanceStub.findYearlyBalance(
                com.sanedge.paymentgateway.pb.card.stats.FindYearBalance.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearBalance>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearBalances(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseMonthBalance>> cardMonthlyBalanceByCard(@Argument CardNumberYearInput input) {
        var resp = cardStatsBalanceStub.findMonthlyBalanceByCardNumber(
                com.sanedge.paymentgateway.pb.card.stats.FindYearBalanceCardNumber.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthBalance>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthBalances(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseYearBalance>> cardYearlyBalanceByCard(@Argument CardNumberYearInput input) {
        var resp = cardStatsBalanceStub.findYearlyBalanceByCardNumber(
                com.sanedge.paymentgateway.pb.card.stats.FindYearBalanceCardNumber.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearBalance>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearBalances(resp.getDataList()))
                .build();
    }

    // ---- stats: topup ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTopupAmount(@Argument YearInput input) {
        var resp = cardStatsTopupStub.findMonthlyTopupAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                .setYear(nvl(input != null ? input.year() : null))
                .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTopupAmount(@Argument YearInput input) {
        var resp = cardStatsTopupStub.findYearlyTopupAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                .setYear(nvl(input != null ? input.year() : null))
                .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTopupAmountByCard(@Argument CardNumberYearInput input) {
        var resp = cardStatsTopupStub.findMonthlyTopupAmountByCardNumber(
                com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTopupAmountByCard(@Argument CardNumberYearInput input) {
        var resp = cardStatsTopupStub.findYearlyTopupAmountByCardNumber(
                com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: transaction ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTransactionAmount(@Argument YearInput input) {
        var resp = cardStatsTransactionStub
                .findMonthlyTransactionAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTransactionAmount(@Argument YearInput input) {
        var resp = cardStatsTransactionStub
                .findYearlyTransactionAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTransactionAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsTransactionStub
                .findMonthlyTransactionAmountByCardNumber(com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber
                        .newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTransactionAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsTransactionStub
                .findYearlyTransactionAmountByCardNumber(com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber
                        .newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: transfer ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTransferAmountSender(@Argument YearInput input) {
        var resp = cardStatsTransferStub
                .findMonthlyTransferSenderAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTransferAmountReceiver(@Argument YearInput input) {
        var resp = cardStatsTransferStub
                .findMonthlyTransferReceiverAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTransferAmountSender(@Argument YearInput input) {
        var resp = cardStatsTransferStub
                .findYearlyTransferSenderAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTransferAmountReceiver(@Argument YearInput input) {
        var resp = cardStatsTransferStub
                .findYearlyTransferReceiverAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTransferAmountByCardSender(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsTransferStub
                .findMonthlyTransferSenderAmountByCardNumber(com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber
                        .newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyTransferAmountByCardReceiver(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsTransferStub
                .findMonthlyTransferReceiverAmountByCardNumber(
                        com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber.newBuilder()
                                .setYear(nvl(input != null ? input.year() : null))
                                .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                                .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTransferAmountByCardSender(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsTransferStub
                .findYearlyTransferSenderAmountByCardNumber(com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber
                        .newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyTransferAmountByCardReceiver(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsTransferStub
                .findYearlyTransferReceiverAmountByCardNumber(
                        com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber.newBuilder()
                                .setYear(nvl(input != null ? input.year() : null))
                                .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                                .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- stats: withdraw ----

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyWithdrawAmount(@Argument YearInput input) {
        var resp = cardStatsWithdrawStub
                .findMonthlyWithdrawAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyWithdrawAmount(@Argument YearInput input) {
        var resp = cardStatsWithdrawStub
                .findYearlyWithdrawAmount(com.sanedge.paymentgateway.pb.card.FindYearAmount.newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseMonthAmount>> cardMonthlyWithdrawAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsWithdrawStub
                .findMonthlyWithdrawAmountByCardNumber(com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber
                        .newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardMonthAmounts(resp.getDataList()))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ApiResponse<List<CardResponseYearAmount>> cardYearlyWithdrawAmountByCard(
            @Argument CardNumberYearInput input) {
        var resp = cardStatsWithdrawStub
                .findYearlyWithdrawAmountByCardNumber(com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber
                        .newBuilder()
                        .setYear(nvl(input != null ? input.year() : null))
                        .setCardNumber(nvl(input != null ? input.cardNumber() : null))
                        .build());
        return ApiResponse.<List<CardResponseYearAmount>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardYearAmounts(resp.getDataList()))
                .build();
    }

    // ---- mutations ----

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CardResponse> createCard(@Argument CreateCardInput input) {
        var resp = cardCommandStub.createCard(com.sanedge.paymentgateway.pb.card.CreateCardRequest.newBuilder()
                .setUserId(nvl(input.userId()))
                .setCardType(nvl(input.cardType()))
                .setExpireDate(ProtoMapper.toTimestamp(java.time.LocalDate.parse(nvl(input.expireDate(), "1970-01-01"))))
                .setCvv(nvl(input.cvv()))
                .setCardProvider(nvl(input.cardProvider()))
                .build());
        return ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CardResponse> updateCard(@Argument UpdateCardInput input) {
        var resp = cardCommandStub.updateCard(com.sanedge.paymentgateway.pb.card.UpdateCardRequest.newBuilder()
                .setCardId(nvl(input.cardId()))
                .setUserId(nvl(input.userId()))
                .setCardType(nvl(input.cardType()))
                .setExpireDate(ProtoMapper.toTimestamp(java.time.LocalDate.parse(nvl(input.expireDate(), "1970-01-01"))))
                .setCvv(nvl(input.cvv()))
                .setCardProvider(nvl(input.cardProvider()))
                .build());
        return ApiResponse.<CardResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.card(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CardResponseDeleteAt> trashCard(@Argument Long id) {
        var resp = cardCommandStub.trashedCard(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(nvl(id))
                .build());
        return ApiResponse.<CardResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CardResponseDeleteAt> restoreCard(@Argument Long id) {
        var resp = cardCommandStub.restoreCard(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(nvl(id))
                .build());
        return ApiResponse.<CardResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.cardDeleteAt(resp.getData()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> deleteCardPermanent(@Argument Long id) {
        var resp = cardCommandStub.deleteCardPermanent(com.sanedge.paymentgateway.pb.card.FindByIdCardRequest.newBuilder()
                .setCardId(nvl(id))
                .build());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> restoreAllCards() {
        var resp = cardCommandStub.restoreAllCard(Empty.getDefaultInstance());
        return ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> deleteAllCardsPermanent() {
        var resp = cardCommandStub.deleteAllCardPermanent(Empty.getDefaultInstance());
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

    private static String nvl(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
