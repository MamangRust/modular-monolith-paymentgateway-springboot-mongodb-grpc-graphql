package com.sanedge.paymentgateway.gateway.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.google.protobuf.Timestamp;
import com.sanedge.paymentgateway.gateway.dto.responses.auth.TokenResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.card.CardResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.card.CardResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.card.dashboard.CardDashboard;
import com.sanedge.paymentgateway.gateway.dto.responses.card.dashboard.CardDashboardCard;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.paymentgateway.gateway.dto.responses.card.stats.balance.CardResponseYearBalance;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.MerchantResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.MerchantTransactionResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.amount.MerchantResponseYearlyAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.merchant.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.paymentgateway.gateway.dto.responses.role.RoleResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.role.RoleResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.SaldoResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.balance.SaldoYearBalanceResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.stats.total_balance.SaldoYearTotalBalanceResponse;
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
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.TransferResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.amount.TransferYearAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseMonthStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseYearStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.stats.status.TransferResponseYearStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.user.UserResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.user.UserResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.WithdrawResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.paymentgateway.gateway.dto.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;

/**
 * Maps generated protobuf response messages onto the gateway-local REST DTOs so
 * that the JSON contract stays identical to the former monolith. Protobuf types
 * are fully qualified to avoid simple-name collisions with the gateway DTOs.
 */
public final class ProtoMapper {

    private ProtoMapper() {
    }

    private static String str(com.google.protobuf.StringValue value) {
        return value == null ? null : value.getValue();
    }

    // ---- timestamps (REST DTO -> proto) ----
    public static Timestamp toTimestamp(LocalDateTime value) {
        if (value == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(value.toEpochSecond(ZoneOffset.UTC))
                .setNanos(value.getNano())
                .build();
    }

    public static Timestamp toTimestamp(LocalDate value) {
        if (value == null) {
            return Timestamp.getDefaultInstance();
        }
        return toTimestamp(value.atStartOfDay());
    }

    // ---- auth ----
    public static TokenResponse token(com.sanedge.paymentgateway.pb.TokenResponse p) {
        if (p == null) {
            return null;
        }
        return TokenResponse.builder()
                .access_token(p.getAccessToken())
                .refresh_token(p.getRefreshToken())
                .build();
    }

    // ---- user ----
    public static UserResponse user(com.sanedge.paymentgateway.pb.user.UserResponse p) {
        if (p == null) {
            return null;
        }
        return UserResponse.builder()
                .id((long) p.getId())
                .username(p.getEmail())
                .firstname(p.getFirstname())
                .lastname(p.getLastname())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static UserResponseDeleteAt userDeleteAt(com.sanedge.paymentgateway.pb.user.UserResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return UserResponseDeleteAt.builder()
                .id((long) p.getId())
                .firstname(p.getFirstname())
                .lastname(p.getLastname())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<UserResponse> users(List<com.sanedge.paymentgateway.pb.user.UserResponse> list) {
        return list.stream().map(ProtoMapper::user).toList();
    }

    public static List<UserResponseDeleteAt> usersDeleteAt(
            List<com.sanedge.paymentgateway.pb.user.UserResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::userDeleteAt).toList();
    }

    // ---- role ----
    public static RoleResponse role(com.sanedge.paymentgateway.pb.role.RoleResponse p) {
        if (p == null) {
            return null;
        }
        return RoleResponse.builder()
                .id((long) p.getId())
                .name(p.getName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static RoleResponseDeleteAt roleDeleteAt(com.sanedge.paymentgateway.pb.role.RoleResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return RoleResponseDeleteAt.builder()
                .id((long) p.getId())
                .name(p.getName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<RoleResponse> roles(List<com.sanedge.paymentgateway.pb.role.RoleResponse> list) {
        return list.stream().map(ProtoMapper::role).toList();
    }

    public static List<RoleResponseDeleteAt> rolesDeleteAt(
            List<com.sanedge.paymentgateway.pb.role.RoleResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::roleDeleteAt).toList();
    }

    // ---- card ----
    public static CardResponse card(com.sanedge.paymentgateway.pb.card.CardResponse p) {
        if (p == null) {
            return null;
        }
        return CardResponse.builder()
                .id((long) p.getId())
                .userId((long) p.getUserId())
                .cardNumber(p.getCardNumber())
                .cardType(p.getCardType())
                .expireDate(p.getExpireDate())
                .cvv(p.getCvv())
                .cardProvider(p.getCardProvider())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static CardResponseDeleteAt cardDeleteAt(com.sanedge.paymentgateway.pb.card.CardResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return CardResponseDeleteAt.builder()
                .id((long) p.getId())
                .userId((long) p.getUserId())
                .cardNumber(p.getCardNumber())
                .cardType(p.getCardType())
                .expireDate(p.getExpireDate())
                .cvv(p.getCvv())
                .cardProvider(p.getCardProvider())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<CardResponse> cards(List<com.sanedge.paymentgateway.pb.card.CardResponse> list) {
        return list.stream().map(ProtoMapper::card).toList();
    }

    public static List<CardResponseDeleteAt> cardsDeleteAt(
            List<com.sanedge.paymentgateway.pb.card.CardResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::cardDeleteAt).toList();
    }

    public static CardDashboard cardDashboard(com.sanedge.paymentgateway.pb.card.CardResponseDashboard p) {
        if (p == null) {
            return null;
        }
        return CardDashboard.builder()
                .totalBalance(p.getTotalBalance())
                .totalTopup(p.getTotalTopup())
                .totalWithdraw(p.getTotalWithdraw())
                .totalTransaction(p.getTotalTransaction())
                .totalTransfer(p.getTotalTransfer())
                .build();
    }

    public static CardDashboardCard cardDashboardCard(
            com.sanedge.paymentgateway.pb.card.CardResponseDashboardCardNumber p) {
        if (p == null) {
            return null;
        }
        return CardDashboardCard.builder()
                .totalBalance(p.getTotalBalance())
                .totalTopup(p.getTotalTopup())
                .totalWithdraw(p.getTotalWithdraw())
                .totalTransaction(p.getTotalTransaction())
                .totalTransferSend(p.getTotalTransferSend())
                .totalTransferReceiver(p.getTotalTransferReceiver())
                .build();
    }

    public static CardResponseMonthAmount cardMonthAmount(
            com.sanedge.paymentgateway.pb.card.CardResponseMonthlyAmount p) {
        return CardResponseMonthAmount.builder()
                .monthName(p.getMonth())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static CardResponseYearAmount cardYearAmount(
            com.sanedge.paymentgateway.pb.card.CardResponseYearlyAmount p) {
        return CardResponseYearAmount.builder()
                .reportYear(p.getYear())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static CardResponseMonthBalance cardMonthBalance(
            com.sanedge.paymentgateway.pb.card.stats.CardResponseMonthlyBalance p) {
        return CardResponseMonthBalance.builder()
                .monthName(p.getMonth())
                .totalBalance(p.getTotalBalance())
                .build();
    }

    public static CardResponseYearBalance cardYearBalance(
            com.sanedge.paymentgateway.pb.card.stats.CardResponseYearlyBalance p) {
        return CardResponseYearBalance.builder()
                .reportYear(p.getYear())
                .totalBalance(p.getTotalBalance())
                .build();
    }

    public static List<CardResponseMonthAmount> cardMonthAmounts(
            List<com.sanedge.paymentgateway.pb.card.CardResponseMonthlyAmount> list) {
        return list.stream().map(ProtoMapper::cardMonthAmount).toList();
    }

    public static List<CardResponseYearAmount> cardYearAmounts(
            List<com.sanedge.paymentgateway.pb.card.CardResponseYearlyAmount> list) {
        return list.stream().map(ProtoMapper::cardYearAmount).toList();
    }

    public static List<CardResponseMonthBalance> cardMonthBalances(
            List<com.sanedge.paymentgateway.pb.card.stats.CardResponseMonthlyBalance> list) {
        return list.stream().map(ProtoMapper::cardMonthBalance).toList();
    }

    public static List<CardResponseYearBalance> cardYearBalances(
            List<com.sanedge.paymentgateway.pb.card.stats.CardResponseYearlyBalance> list) {
        return list.stream().map(ProtoMapper::cardYearBalance).toList();
    }

    // ---- merchant ----
    public static MerchantResponse merchant(com.sanedge.paymentgateway.pb.merchant.MerchantResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantResponse.builder()
                .id((long) p.getId())
                .name(p.getName())
                .userId((long) p.getUserId())
                .apiKey(p.getApiKey())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static MerchantResponseDeleteAt merchantDeleteAt(
            com.sanedge.paymentgateway.pb.merchant.MerchantResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return MerchantResponseDeleteAt.builder()
                .id((long) p.getId())
                .name(p.getName())
                .userId((long) p.getUserId())
                .apiKey(p.getApiKey())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static MerchantTransactionResponse merchantTransaction(
            com.sanedge.paymentgateway.pb.merchant.MerchantTransactionResponse p) {
        if (p == null) {
            return null;
        }
        return MerchantTransactionResponse.builder()
                .id((long) p.getId())
                .cardNumber(p.getCardNumber())
                .amount((long) p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .merchantId((long) p.getMerchantId())
                .merchantName(p.getMerchantName())
                .transactionTime(p.getTransactionTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<MerchantResponse> merchants(List<com.sanedge.paymentgateway.pb.merchant.MerchantResponse> list) {
        return list.stream().map(ProtoMapper::merchant).toList();
    }

    public static List<MerchantResponseDeleteAt> merchantsDeleteAt(
            List<com.sanedge.paymentgateway.pb.merchant.MerchantResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::merchantDeleteAt).toList();
    }

    public static List<MerchantTransactionResponse> merchantTransactions(
            List<com.sanedge.paymentgateway.pb.merchant.MerchantTransactionResponse> list) {
        return list.stream().map(ProtoMapper::merchantTransaction).toList();
    }

    public static MerchantResponseMonthlyAmount merchantMonthAmount(
            com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyAmount p) {
        return MerchantResponseMonthlyAmount.builder()
                .monthName(p.getMonth())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static MerchantResponseYearlyAmount merchantYearAmount(
            com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyAmount p) {
        return MerchantResponseYearlyAmount.builder()
                .reportYear(p.getYear())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static MerchantResponseMonthlyPaymentMethod merchantMonthMethod(
            com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyPaymentMethod p) {
        return MerchantResponseMonthlyPaymentMethod.builder()
                .monthName(p.getMonth())
                .paymentMethod(p.getPaymentMethod())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static MerchantResponseYearlyPaymentMethod merchantYearMethod(
            com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyPaymentMethod p) {
        return MerchantResponseYearlyPaymentMethod.builder()
                .reportYear(p.getYear())
                .paymentMethod(p.getPaymentMethod())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static MerchantResponseMonthlyTotalAmount merchantMonthTotalAmount(
            com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyTotalAmount p) {
        return MerchantResponseMonthlyTotalAmount.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static MerchantResponseYearlyTotalAmount merchantYearTotalAmount(
            com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyTotalAmount p) {
        return MerchantResponseYearlyTotalAmount.builder()
                .reportYear(p.getYear())
                .totalAmount(p.getTotalAmount())
                .build();
    }

    public static List<MerchantResponseMonthlyAmount> merchantMonthAmounts(
            List<com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyAmount> list) {
        return list.stream().map(ProtoMapper::merchantMonthAmount).toList();
    }

    public static List<MerchantResponseYearlyAmount> merchantYearAmounts(
            List<com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyAmount> list) {
        return list.stream().map(ProtoMapper::merchantYearAmount).toList();
    }

    public static List<MerchantResponseMonthlyPaymentMethod> merchantMonthMethods(
            List<com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyPaymentMethod> list) {
        return list.stream().map(ProtoMapper::merchantMonthMethod).toList();
    }

    public static List<MerchantResponseYearlyPaymentMethod> merchantYearMethods(
            List<com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyPaymentMethod> list) {
        return list.stream().map(ProtoMapper::merchantYearMethod).toList();
    }

    public static List<MerchantResponseMonthlyTotalAmount> merchantMonthTotalAmounts(
            List<com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyTotalAmount> list) {
        return list.stream().map(ProtoMapper::merchantMonthTotalAmount).toList();
    }

    public static List<MerchantResponseYearlyTotalAmount> merchantYearTotalAmounts(
            List<com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyTotalAmount> list) {
        return list.stream().map(ProtoMapper::merchantYearTotalAmount).toList();
    }

    // ---- saldo ----
    public static SaldoResponse saldo(com.sanedge.paymentgateway.pb.saldo.SaldoResponse p) {
        if (p == null) {
            return null;
        }
        return SaldoResponse.builder()
                .id((long) p.getSaldoId())
                .cardNumber(p.getCardNumber())
                .totalBalance((long) p.getTotalBalance())
                .withdrawAmount((long) p.getWithdrawAmount())
                .withdrawTime(p.getWithdrawTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static SaldoResponseDeleteAt saldoDeleteAt(com.sanedge.paymentgateway.pb.saldo.SaldoResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return SaldoResponseDeleteAt.builder()
                .id((long) p.getSaldoId())
                .cardNumber(p.getCardNumber())
                .totalBalance((long) p.getTotalBalance())
                .withdrawAmount((long) p.getWithdrawAmount())
                .withdrawTime(p.getWithdrawTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<SaldoResponse> saldos(List<com.sanedge.paymentgateway.pb.saldo.SaldoResponse> list) {
        return list.stream().map(ProtoMapper::saldo).toList();
    }

    public static List<SaldoResponseDeleteAt> saldosDeleteAt(
            List<com.sanedge.paymentgateway.pb.saldo.SaldoResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::saldoDeleteAt).toList();
    }

    public static SaldoMonthBalanceResponse saldoMonthBalance(
            com.sanedge.paymentgateway.pb.saldo.stats.SaldoMonthBalanceResponse p) {
        return SaldoMonthBalanceResponse.builder()
                .monthName(p.getMonth())
                .totalBalance((long) p.getTotalBalance())
                .build();
    }

    public static SaldoYearBalanceResponse saldoYearBalance(
            com.sanedge.paymentgateway.pb.saldo.stats.SaldoYearBalanceResponse p) {
        return SaldoYearBalanceResponse.builder()
                .reportYear(p.getYear())
                .totalBalance((long) p.getTotalBalance())
                .build();
    }

    public static SaldoMonthTotalBalanceResponse saldoMonthTotalBalance(
            com.sanedge.paymentgateway.pb.saldo.stats.SaldoMonthTotalBalanceResponse p) {
        return SaldoMonthTotalBalanceResponse.builder()
                .monthName(p.getMonth())
                .reportYear(p.getYear())
                .totalBalance((long) p.getTotalBalance())
                .build();
    }

    public static SaldoYearTotalBalanceResponse saldoYearTotalBalance(
            com.sanedge.paymentgateway.pb.saldo.stats.SaldoYearTotalBalanceResponse p) {
        return SaldoYearTotalBalanceResponse.builder()
                .reportYear(p.getYear())
                .totalBalance((long) p.getTotalBalance())
                .build();
    }

    public static List<SaldoMonthBalanceResponse> saldoMonthBalances(
            List<com.sanedge.paymentgateway.pb.saldo.stats.SaldoMonthBalanceResponse> list) {
        return list.stream().map(ProtoMapper::saldoMonthBalance).toList();
    }

    public static List<SaldoYearBalanceResponse> saldoYearBalances(
            List<com.sanedge.paymentgateway.pb.saldo.stats.SaldoYearBalanceResponse> list) {
        return list.stream().map(ProtoMapper::saldoYearBalance).toList();
    }

    public static List<SaldoMonthTotalBalanceResponse> saldoMonthTotalBalances(
            List<com.sanedge.paymentgateway.pb.saldo.stats.SaldoMonthTotalBalanceResponse> list) {
        return list.stream().map(ProtoMapper::saldoMonthTotalBalance).toList();
    }

    public static List<SaldoYearTotalBalanceResponse> saldoYearTotalBalances(
            List<com.sanedge.paymentgateway.pb.saldo.stats.SaldoYearTotalBalanceResponse> list) {
        return list.stream().map(ProtoMapper::saldoYearTotalBalance).toList();
    }

    // ---- topup ----
    public static TopupResponse topup(com.sanedge.paymentgateway.pb.topup.TopupResponse p) {
        if (p == null) {
            return null;
        }
        return TopupResponse.builder()
                .id((long) p.getId())
                .cardNumber(p.getCardNumber())
                .topupNo(p.getTopupNo())
                .topupAmount((long) p.getTopupAmount())
                .topupMethod(p.getTopupMethod())
                .topupTime(p.getTopupTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static TopupResponseDeleteAt topupDeleteAt(com.sanedge.paymentgateway.pb.topup.TopupResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return TopupResponseDeleteAt.builder()
                .id((long) p.getId())
                .cardNumber(p.getCardNumber())
                .topupNo(p.getTopupNo())
                .topupAmount((long) p.getTopupAmount())
                .topupMethod(p.getTopupMethod())
                .topupTime(p.getTopupTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<TopupResponse> topups(List<com.sanedge.paymentgateway.pb.topup.TopupResponse> list) {
        return list.stream().map(ProtoMapper::topup).toList();
    }

    public static List<TopupResponseDeleteAt> topupsDeleteAt(
            List<com.sanedge.paymentgateway.pb.topup.TopupResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::topupDeleteAt).toList();
    }

    public static TopupMonthAmountResponse topupMonthAmount(
            com.sanedge.paymentgateway.pb.topup.stats.TopupMonthAmountResponse p) {
        return TopupMonthAmountResponse.builder()
                .monthName(p.getMonth())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupYearlyAmountResponse topupYearAmount(
            com.sanedge.paymentgateway.pb.topup.stats.TopupYearlyAmountResponse p) {
        return TopupYearlyAmountResponse.builder()
                .reportYear(p.getYear())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupMonthMethodResponse topupMonthMethod(
            com.sanedge.paymentgateway.pb.topup.stats.TopupMonthMethodResponse p) {
        return TopupMonthMethodResponse.builder()
                .monthName(p.getMonth())
                .topupMethod(p.getTopupMethod())
                .totalTopups((long) p.getTotalTopups())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupYearlyMethodResponse topupYearMethod(
            com.sanedge.paymentgateway.pb.topup.stats.TopupYearlyMethodResponse p) {
        return TopupYearlyMethodResponse.builder()
                .reportYear(p.getYear())
                .topupMethod(p.getTopupMethod())
                .totalTopups((long) p.getTotalTopups())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupResponseMonthStatusSuccess topupMonthStatusSuccess(
            com.sanedge.paymentgateway.pb.topup.stats.TopupMonthStatusSuccessResponse p) {
        return TopupResponseMonthStatusSuccess.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupResponseYearStatusSuccess topupYearStatusSuccess(
            com.sanedge.paymentgateway.pb.topup.stats.TopupYearStatusSuccessResponse p) {
        return TopupResponseYearStatusSuccess.builder()
                .reportYear(p.getYear())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupResponseMonthStatusFailed topupMonthStatusFailed(
            com.sanedge.paymentgateway.pb.topup.stats.TopupMonthStatusFailedResponse p) {
        return TopupResponseMonthStatusFailed.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TopupResponseYearStatusFailed topupYearStatusFailed(
            com.sanedge.paymentgateway.pb.topup.stats.TopupYearStatusFailedResponse p) {
        return TopupResponseYearStatusFailed.builder()
                .reportYear(p.getYear())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static List<TopupMonthAmountResponse> topupMonthAmounts(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupMonthAmountResponse> list) {
        return list.stream().map(ProtoMapper::topupMonthAmount).toList();
    }

    public static List<TopupYearlyAmountResponse> topupYearAmounts(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupYearlyAmountResponse> list) {
        return list.stream().map(ProtoMapper::topupYearAmount).toList();
    }

    public static List<TopupMonthMethodResponse> topupMonthMethods(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupMonthMethodResponse> list) {
        return list.stream().map(ProtoMapper::topupMonthMethod).toList();
    }

    public static List<TopupYearlyMethodResponse> topupYearMethods(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupYearlyMethodResponse> list) {
        return list.stream().map(ProtoMapper::topupYearMethod).toList();
    }

    public static List<TopupResponseMonthStatusSuccess> topupMonthStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupMonthStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::topupMonthStatusSuccess).toList();
    }

    public static List<TopupResponseYearStatusSuccess> topupYearStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupYearStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::topupYearStatusSuccess).toList();
    }

    public static List<TopupResponseMonthStatusFailed> topupMonthStatusFailures(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupMonthStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::topupMonthStatusFailed).toList();
    }

    public static List<TopupResponseYearStatusFailed> topupYearStatusFailures(
            List<com.sanedge.paymentgateway.pb.topup.stats.TopupYearStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::topupYearStatusFailed).toList();
    }

    // ---- transaction ----
    public static TransactionResponse transaction(com.sanedge.paymentgateway.pb.transaction.TransactionResponse p) {
        if (p == null) {
            return null;
        }
        return TransactionResponse.builder()
                .id((long) p.getId())
                .transactionNo(p.getTransactionNo())
                .cardNumber(p.getCardNumber())
                .amount((long) p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .merchantId((long) p.getMerchantId())
                .transactionTime(p.getTransactionTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static TransactionResponseDeleteAt transactionDeleteAt(
            com.sanedge.paymentgateway.pb.transaction.TransactionResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return TransactionResponseDeleteAt.builder()
                .id((long) p.getId())
                .transactionNo(p.getTransactionNo())
                .cardNumber(p.getCardNumber())
                .amount((long) p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .merchantId((long) p.getMerchantId())
                .transactionTime(p.getTransactionTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<TransactionResponse> transactions(
            List<com.sanedge.paymentgateway.pb.transaction.TransactionResponse> list) {
        return list.stream().map(ProtoMapper::transaction).toList();
    }

    public static List<TransactionResponseDeleteAt> transactionsDeleteAt(
            List<com.sanedge.paymentgateway.pb.transaction.TransactionResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::transactionDeleteAt).toList();
    }

    public static TransactionMonthAmountResponse transactionMonthAmount(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthAmountResponse p) {
        return TransactionMonthAmountResponse.builder()
                .monthName(p.getMonth())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionYearlyAmountResponse transactionYearAmount(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearlyAmountResponse p) {
        return TransactionYearlyAmountResponse.builder()
                .reportYear(p.getYear())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionMonthMethodResponse transactionMonthMethod(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthMethodResponse p) {
        return TransactionMonthMethodResponse.builder()
                .monthName(p.getMonth())
                .paymentMethod(p.getPaymentMethod())
                .totalTransactions((long) p.getTotalTransactions())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionYearMethodResponse transactionYearMethod(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearMethodResponse p) {
        return TransactionYearMethodResponse.builder()
                .reportYear(p.getYear())
                .paymentMethod(p.getPaymentMethod())
                .totalTransactions((long) p.getTotalTransactions())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionResponseMonthStatusSuccess transactionMonthStatusSuccess(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthStatusSuccessResponse p) {
        return TransactionResponseMonthStatusSuccess.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionResponseYearStatusSuccess transactionYearStatusSuccess(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearStatusSuccessResponse p) {
        return TransactionResponseYearStatusSuccess.builder()
                .reportYear(p.getYear())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionResponseMonthStatusFailed transactionMonthStatusFailed(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthStatusFailedResponse p) {
        return TransactionResponseMonthStatusFailed.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransactionResponseYearStatusFailed transactionYearStatusFailed(
            com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearStatusFailedResponse p) {
        return TransactionResponseYearStatusFailed.builder()
                .reportYear(p.getYear())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static List<TransactionMonthAmountResponse> transactionMonthAmounts(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthAmountResponse> list) {
        return list.stream().map(ProtoMapper::transactionMonthAmount).toList();
    }

    public static List<TransactionYearlyAmountResponse> transactionYearAmounts(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearlyAmountResponse> list) {
        return list.stream().map(ProtoMapper::transactionYearAmount).toList();
    }

    public static List<TransactionMonthMethodResponse> transactionMonthMethods(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthMethodResponse> list) {
        return list.stream().map(ProtoMapper::transactionMonthMethod).toList();
    }

    public static List<TransactionYearMethodResponse> transactionYearMethods(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearMethodResponse> list) {
        return list.stream().map(ProtoMapper::transactionYearMethod).toList();
    }

    public static List<TransactionResponseMonthStatusSuccess> transactionMonthStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::transactionMonthStatusSuccess).toList();
    }

    public static List<TransactionResponseYearStatusSuccess> transactionYearStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::transactionYearStatusSuccess).toList();
    }

    public static List<TransactionResponseMonthStatusFailed> transactionMonthStatusFailures(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::transactionMonthStatusFailed).toList();
    }

    public static List<TransactionResponseYearStatusFailed> transactionYearStatusFailures(
            List<com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::transactionYearStatusFailed).toList();
    }

    // ---- transfer ----
    public static TransferResponse transfer(com.sanedge.paymentgateway.pb.transfer.TransferResponse p) {
        if (p == null) {
            return null;
        }
        return TransferResponse.builder()
                .id((long) p.getId())
                .transferNo(p.getTransferNo())
                .transferFrom(p.getTransferFrom())
                .transferTo(p.getTransferTo())
                .transferAmount((long) p.getTransferAmount())
                .transferTime(p.getTransferTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static TransferResponseDeleteAt transferDeleteAt(
            com.sanedge.paymentgateway.pb.transfer.TransferResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return TransferResponseDeleteAt.builder()
                .id((long) p.getId())
                .transferNo(p.getTransferNo())
                .transferFrom(p.getTransferFrom())
                .transferTo(p.getTransferTo())
                .transferAmount((long) p.getTransferAmount())
                .transferTime(p.getTransferTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<TransferResponse> transfers(List<com.sanedge.paymentgateway.pb.transfer.TransferResponse> list) {
        return list.stream().map(ProtoMapper::transfer).toList();
    }

    public static List<TransferResponseDeleteAt> transfersDeleteAt(
            List<com.sanedge.paymentgateway.pb.transfer.TransferResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::transferDeleteAt).toList();
    }

    public static TransferMonthAmountResponse transferMonthAmount(
            com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthAmountResponse p) {
        return TransferMonthAmountResponse.builder()
                .monthName(p.getMonth())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransferYearAmountResponse transferYearAmount(
            com.sanedge.paymentgateway.pb.transfer.stats.TransferYearAmountResponse p) {
        return TransferYearAmountResponse.builder()
                .reportYear(p.getYear())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransferResponseMonthStatusSuccess transferMonthStatusSuccess(
            com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthStatusSuccessResponse p) {
        return TransferResponseMonthStatusSuccess.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransferResponseYearStatusSuccess transferYearStatusSuccess(
            com.sanedge.paymentgateway.pb.transfer.stats.TransferYearStatusSuccessResponse p) {
        return TransferResponseYearStatusSuccess.builder()
                .reportYear(p.getYear())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransferResponseMonthStatusFailed transferMonthStatusFailed(
            com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthStatusFailedResponse p) {
        return TransferResponseMonthStatusFailed.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static TransferResponseYearStatusFailed transferYearStatusFailed(
            com.sanedge.paymentgateway.pb.transfer.stats.TransferYearStatusFailedResponse p) {
        return TransferResponseYearStatusFailed.builder()
                .reportYear(p.getYear())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static List<TransferMonthAmountResponse> transferMonthAmounts(
            List<com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthAmountResponse> list) {
        return list.stream().map(ProtoMapper::transferMonthAmount).toList();
    }

    public static List<TransferYearAmountResponse> transferYearAmounts(
            List<com.sanedge.paymentgateway.pb.transfer.stats.TransferYearAmountResponse> list) {
        return list.stream().map(ProtoMapper::transferYearAmount).toList();
    }

    public static List<TransferResponseMonthStatusSuccess> transferMonthStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::transferMonthStatusSuccess).toList();
    }

    public static List<TransferResponseYearStatusSuccess> transferYearStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.transfer.stats.TransferYearStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::transferYearStatusSuccess).toList();
    }

    public static List<TransferResponseMonthStatusFailed> transferMonthStatusFailures(
            List<com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::transferMonthStatusFailed).toList();
    }

    public static List<TransferResponseYearStatusFailed> transferYearStatusFailures(
            List<com.sanedge.paymentgateway.pb.transfer.stats.TransferYearStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::transferYearStatusFailed).toList();
    }

    // ---- withdraw ----
    public static WithdrawResponse withdraw(com.sanedge.paymentgateway.pb.withdraw.WithdrawResponse p) {
        if (p == null) {
            return null;
        }
        return WithdrawResponse.builder()
                .id((long) p.getWithdrawId())
                .withdrawNo(p.getWithdrawNo())
                .cardNumber(p.getCardNumber())
                .withdrawAmount((long) p.getWithdrawAmount())
                .withdrawTime(p.getWithdrawTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static WithdrawResponseDeleteAt withdrawDeleteAt(
            com.sanedge.paymentgateway.pb.withdraw.WithdrawResponseDeleteAt p) {
        if (p == null) {
            return null;
        }
        return WithdrawResponseDeleteAt.builder()
                .id((long) p.getWithdrawId())
                .withdrawNo(p.getWithdrawNo())
                .cardNumber(p.getCardNumber())
                .withdrawAmount((long) p.getWithdrawAmount())
                .withdrawTime(p.getWithdrawTime())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(str(p.getDeletedAt()))
                .build();
    }

    public static List<WithdrawResponse> withdraws(List<com.sanedge.paymentgateway.pb.withdraw.WithdrawResponse> list) {
        return list.stream().map(ProtoMapper::withdraw).toList();
    }

    public static List<WithdrawResponseDeleteAt> withdrawsDeleteAt(
            List<com.sanedge.paymentgateway.pb.withdraw.WithdrawResponseDeleteAt> list) {
        return list.stream().map(ProtoMapper::withdrawDeleteAt).toList();
    }

    public static WithdrawMonthlyAmountResponse withdrawMonthAmount(
            com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthlyAmountResponse p) {
        return WithdrawMonthlyAmountResponse.builder()
                .monthName(p.getMonth())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static WithdrawYearlyAmountResponse withdrawYearAmount(
            com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearlyAmountResponse p) {
        return WithdrawYearlyAmountResponse.builder()
                .reportYear(p.getYear())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static WithdrawResponseMonthStatusSuccess withdrawMonthStatusSuccess(
            com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthStatusSuccessResponse p) {
        return WithdrawResponseMonthStatusSuccess.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static WithdrawResponseYearStatusSuccess withdrawYearStatusSuccess(
            com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearStatusSuccessResponse p) {
        return WithdrawResponseYearStatusSuccess.builder()
                .reportYear(p.getYear())
                .totalSuccess((long) p.getTotalSuccess())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static WithdrawResponseMonthStatusFailed withdrawMonthStatusFailed(
            com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthStatusFailedResponse p) {
        return WithdrawResponseMonthStatusFailed.builder()
                .reportYear(p.getYear())
                .monthName(p.getMonth())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static WithdrawResponseYearStatusFailed withdrawYearStatusFailed(
            com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearStatusFailedResponse p) {
        return WithdrawResponseYearStatusFailed.builder()
                .reportYear(p.getYear())
                .totalFailed((long) p.getTotalFailed())
                .totalAmount((long) p.getTotalAmount())
                .build();
    }

    public static List<WithdrawMonthlyAmountResponse> withdrawMonthAmounts(
            List<com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthlyAmountResponse> list) {
        return list.stream().map(ProtoMapper::withdrawMonthAmount).toList();
    }

    public static List<WithdrawYearlyAmountResponse> withdrawYearAmounts(
            List<com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearlyAmountResponse> list) {
        return list.stream().map(ProtoMapper::withdrawYearAmount).toList();
    }

    public static List<WithdrawResponseMonthStatusSuccess> withdrawMonthStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::withdrawMonthStatusSuccess).toList();
    }

    public static List<WithdrawResponseYearStatusSuccess> withdrawYearStatusSuccesses(
            List<com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearStatusSuccessResponse> list) {
        return list.stream().map(ProtoMapper::withdrawYearStatusSuccess).toList();
    }

    public static List<WithdrawResponseMonthStatusFailed> withdrawMonthStatusFailures(
            List<com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::withdrawMonthStatusFailed).toList();
    }

    public static List<WithdrawResponseYearStatusFailed> withdrawYearStatusFailures(
            List<com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearStatusFailedResponse> list) {
        return list.stream().map(ProtoMapper::withdrawYearStatusFailed).toList();
    }
}
