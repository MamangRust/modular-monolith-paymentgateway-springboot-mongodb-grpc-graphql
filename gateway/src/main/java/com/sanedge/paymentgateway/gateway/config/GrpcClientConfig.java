package com.sanedge.paymentgateway.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sanedge.paymentgateway.pb.AuthServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardDashboardServiceGrpc;
import com.sanedge.paymentgateway.pb.card.CardQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsBalanceServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTopupServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTransactionServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTransferServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsWithdrawServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.MerchantCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.MerchantQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.MerchantTransactionServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsTotalAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.role.RoleCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.role.RoleServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.SaldoCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.SaldoQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoStatsBalanceServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoStatsTotalBalanceGrpc;
import com.sanedge.paymentgateway.pb.topup.TopupCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.TopupQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.TransactionCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.TransactionQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.TransferCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.TransferQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.user.UserCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.user.UserQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.WithdrawCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.WithdrawQueryServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawStatsStatusServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

/**
 * One plaintext {@link ManagedChannel} per downstream domain service plus the
 * blocking stubs used by the REST controllers.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.role.host:localhost}")
    private String roleHost;
    @Value("${grpc.client.role.port:9006}")
    private int rolePort;

    @Value("${grpc.client.user.host:localhost}")
    private String userHost;
    @Value("${grpc.client.user.port:9011}")
    private int userPort;

    @Value("${grpc.client.merchant.host:localhost}")
    private String merchantHost;
    @Value("${grpc.client.merchant.port:9005}")
    private int merchantPort;

    @Value("${grpc.client.card.host:localhost}")
    private String cardHost;
    @Value("${grpc.client.card.port:9004}")
    private int cardPort;

    @Value("${grpc.client.saldo.host:localhost}")
    private String saldoHost;
    @Value("${grpc.client.saldo.port:9007}")
    private int saldoPort;

    @Value("${grpc.client.topup.host:localhost}")
    private String topupHost;
    @Value("${grpc.client.topup.port:9008}")
    private int topupPort;

    @Value("${grpc.client.transaction.host:localhost}")
    private String transactionHost;
    @Value("${grpc.client.transaction.port:9009}")
    private int transactionPort;

    @Value("${grpc.client.transfer.host:localhost}")
    private String transferHost;
    @Value("${grpc.client.transfer.port:9010}")
    private int transferPort;

    @Value("${grpc.client.withdraw.host:localhost}")
    private String withdrawHost;
    @Value("${grpc.client.withdraw.port:9013}")
    private int withdrawPort;

    @Value("${grpc.client.auth.host:localhost}")
    private String authHost;
    @Value("${grpc.client.auth.port:9012}")
    private int authPort;

    private ManagedChannel channel(String host, int port) {
        return NettyChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    // --- channels ---
    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel roleChannel() {
        return channel(roleHost, rolePort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel userChannel() {
        return channel(userHost, userPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel merchantChannel() {
        return channel(merchantHost, merchantPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel cardChannel() {
        return channel(cardHost, cardPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel saldoChannel() {
        return channel(saldoHost, saldoPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel topupChannel() {
        return channel(topupHost, topupPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel transactionChannel() {
        return channel(transactionHost, transactionPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel transferChannel() {
        return channel(transferHost, transferPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel withdrawChannel() {
        return channel(withdrawHost, withdrawPort);
    }

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel authChannel() {
        return channel(authHost, authPort);
    }

    // --- auth ---
    @Bean
    public AuthServiceGrpc.AuthServiceBlockingStub authStub(ManagedChannel authChannel) {
        return AuthServiceGrpc.newBlockingStub(authChannel);
    }

    // --- role ---
    @Bean
    public RoleServiceGrpc.RoleServiceBlockingStub roleQueryStub(ManagedChannel roleChannel) {
        return RoleServiceGrpc.newBlockingStub(roleChannel);
    }

    @Bean
    public RoleCommandServiceGrpc.RoleCommandServiceBlockingStub roleCommandStub(ManagedChannel roleChannel) {
        return RoleCommandServiceGrpc.newBlockingStub(roleChannel);
    }

    // --- user ---
    @Bean
    public UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub(ManagedChannel userChannel) {
        return UserQueryServiceGrpc.newBlockingStub(userChannel);
    }

    @Bean
    public UserCommandServiceGrpc.UserCommandServiceBlockingStub userCommandStub(ManagedChannel userChannel) {
        return UserCommandServiceGrpc.newBlockingStub(userChannel);
    }

    // --- merchant ---
    @Bean
    public MerchantQueryServiceGrpc.MerchantQueryServiceBlockingStub merchantQueryStub(ManagedChannel merchantChannel) {
        return MerchantQueryServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantCommandServiceGrpc.MerchantCommandServiceBlockingStub merchantCommandStub(
            ManagedChannel merchantChannel) {
        return MerchantCommandServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantTransactionServiceGrpc.MerchantTransactionServiceBlockingStub merchantTransactionStub(
            ManagedChannel merchantChannel) {
        return MerchantTransactionServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantStatsAmountServiceGrpc.MerchantStatsAmountServiceBlockingStub merchantStatsAmountStub(
            ManagedChannel merchantChannel) {
        return MerchantStatsAmountServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantStatsMethodServiceGrpc.MerchantStatsMethodServiceBlockingStub merchantStatsMethodStub(
            ManagedChannel merchantChannel) {
        return MerchantStatsMethodServiceGrpc.newBlockingStub(merchantChannel);
    }

    @Bean
    public MerchantStatsTotalAmountServiceGrpc.MerchantStatsTotalAmountServiceBlockingStub merchantStatsTotalAmountStub(
            ManagedChannel merchantChannel) {
        return MerchantStatsTotalAmountServiceGrpc.newBlockingStub(merchantChannel);
    }

    // --- card ---
    @Bean
    public CardQueryServiceGrpc.CardQueryServiceBlockingStub cardQueryStub(ManagedChannel cardChannel) {
        return CardQueryServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardCommandServiceGrpc.CardCommandServiceBlockingStub cardCommandStub(ManagedChannel cardChannel) {
        return CardCommandServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardDashboardServiceGrpc.CardDashboardServiceBlockingStub cardDashboardStub(ManagedChannel cardChannel) {
        return CardDashboardServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardStatsBalanceServiceGrpc.CardStatsBalanceServiceBlockingStub cardStatsBalanceStub(
            ManagedChannel cardChannel) {
        return CardStatsBalanceServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardStatsTopupServiceGrpc.CardStatsTopupServiceBlockingStub cardStatsTopupStub(ManagedChannel cardChannel) {
        return CardStatsTopupServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardStatsTransactionServiceGrpc.CardStatsTransactionServiceBlockingStub cardStatsTransactionStub(
            ManagedChannel cardChannel) {
        return CardStatsTransactionServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardStatsTransferServiceGrpc.CardStatsTransferServiceBlockingStub cardStatsTransferStub(
            ManagedChannel cardChannel) {
        return CardStatsTransferServiceGrpc.newBlockingStub(cardChannel);
    }

    @Bean
    public CardStatsWithdrawServiceGrpc.CardStatsWithdrawServiceBlockingStub cardStatsWithdrawStub(
            ManagedChannel cardChannel) {
        return CardStatsWithdrawServiceGrpc.newBlockingStub(cardChannel);
    }

    // --- saldo ---
    @Bean
    public SaldoQueryServiceGrpc.SaldoQueryServiceBlockingStub saldoQueryStub(ManagedChannel saldoChannel) {
        return SaldoQueryServiceGrpc.newBlockingStub(saldoChannel);
    }

    @Bean
    public SaldoCommandServiceGrpc.SaldoCommandServiceBlockingStub saldoCommandStub(ManagedChannel saldoChannel) {
        return SaldoCommandServiceGrpc.newBlockingStub(saldoChannel);
    }

    @Bean
    public SaldoStatsBalanceServiceGrpc.SaldoStatsBalanceServiceBlockingStub saldoStatsBalanceStub(
            ManagedChannel saldoChannel) {
        return SaldoStatsBalanceServiceGrpc.newBlockingStub(saldoChannel);
    }

    @Bean
    public SaldoStatsTotalBalanceGrpc.SaldoStatsTotalBalanceBlockingStub saldoStatsTotalBalanceStub(
            ManagedChannel saldoChannel) {
        return SaldoStatsTotalBalanceGrpc.newBlockingStub(saldoChannel);
    }

    // --- topup ---
    @Bean
    public TopupQueryServiceGrpc.TopupQueryServiceBlockingStub topupQueryStub(ManagedChannel topupChannel) {
        return TopupQueryServiceGrpc.newBlockingStub(topupChannel);
    }

    @Bean
    public TopupCommandServiceGrpc.TopupCommandServiceBlockingStub topupCommandStub(ManagedChannel topupChannel) {
        return TopupCommandServiceGrpc.newBlockingStub(topupChannel);
    }

    @Bean
    public TopupStatsAmountServiceGrpc.TopupStatsAmountServiceBlockingStub topupStatsAmountStub(
            ManagedChannel topupChannel) {
        return TopupStatsAmountServiceGrpc.newBlockingStub(topupChannel);
    }

    @Bean
    public TopupStatsMethodServiceGrpc.TopupStatsMethodServiceBlockingStub topupStatsMethodStub(
            ManagedChannel topupChannel) {
        return TopupStatsMethodServiceGrpc.newBlockingStub(topupChannel);
    }

    @Bean
    public TopupStatsStatusServiceGrpc.TopupStatsStatusServiceBlockingStub topupStatsStatusStub(
            ManagedChannel topupChannel) {
        return TopupStatsStatusServiceGrpc.newBlockingStub(topupChannel);
    }

    // --- transaction ---
    @Bean
    public TransactionQueryServiceGrpc.TransactionQueryServiceBlockingStub transactionQueryStub(
            ManagedChannel transactionChannel) {
        return TransactionQueryServiceGrpc.newBlockingStub(transactionChannel);
    }

    @Bean
    public TransactionCommandServiceGrpc.TransactionCommandServiceBlockingStub transactionCommandStub(
            ManagedChannel transactionChannel) {
        return TransactionCommandServiceGrpc.newBlockingStub(transactionChannel);
    }

    @Bean
    public TransactionStatsAmountServiceGrpc.TransactionStatsAmountServiceBlockingStub transactionStatsAmountStub(
            ManagedChannel transactionChannel) {
        return TransactionStatsAmountServiceGrpc.newBlockingStub(transactionChannel);
    }

    @Bean
    public TransactionStatsMethodServiceGrpc.TransactionStatsMethodServiceBlockingStub transactionStatsMethodStub(
            ManagedChannel transactionChannel) {
        return TransactionStatsMethodServiceGrpc.newBlockingStub(transactionChannel);
    }

    @Bean
    public TransactionStatsStatusServiceGrpc.TransactionStatsStatusServiceBlockingStub transactionStatsStatusStub(
            ManagedChannel transactionChannel) {
        return TransactionStatsStatusServiceGrpc.newBlockingStub(transactionChannel);
    }

    // --- transfer ---
    @Bean
    public TransferQueryServiceGrpc.TransferQueryServiceBlockingStub transferQueryStub(ManagedChannel transferChannel) {
        return TransferQueryServiceGrpc.newBlockingStub(transferChannel);
    }

    @Bean
    public TransferCommandServiceGrpc.TransferCommandServiceBlockingStub transferCommandStub(
            ManagedChannel transferChannel) {
        return TransferCommandServiceGrpc.newBlockingStub(transferChannel);
    }

    @Bean
    public TransferStatsAmountServiceGrpc.TransferStatsAmountServiceBlockingStub transferStatsAmountStub(
            ManagedChannel transferChannel) {
        return TransferStatsAmountServiceGrpc.newBlockingStub(transferChannel);
    }

    @Bean
    public TransferStatsStatusServiceGrpc.TransferStatsStatusServiceBlockingStub transferStatsStatusStub(
            ManagedChannel transferChannel) {
        return TransferStatsStatusServiceGrpc.newBlockingStub(transferChannel);
    }

    // --- withdraw ---
    @Bean
    public WithdrawQueryServiceGrpc.WithdrawQueryServiceBlockingStub withdrawQueryStub(ManagedChannel withdrawChannel) {
        return WithdrawQueryServiceGrpc.newBlockingStub(withdrawChannel);
    }

    @Bean
    public WithdrawCommandServiceGrpc.WithdrawCommandServiceBlockingStub withdrawCommandStub(
            ManagedChannel withdrawChannel) {
        return WithdrawCommandServiceGrpc.newBlockingStub(withdrawChannel);
    }

    @Bean
    public WithdrawStatsAmountServiceGrpc.WithdrawStatsAmountServiceBlockingStub withdrawStatsAmountStub(
            ManagedChannel withdrawChannel) {
        return WithdrawStatsAmountServiceGrpc.newBlockingStub(withdrawChannel);
    }

    @Bean
    public WithdrawStatsStatusServiceGrpc.WithdrawStatsStatusServiceBlockingStub withdrawStatsStatusStub(
            ManagedChannel withdrawChannel) {
        return WithdrawStatsStatusServiceGrpc.newBlockingStub(withdrawChannel);
    }
}
