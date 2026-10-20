package com.sanedge.paymentgateway.transaction.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.transaction.FindByYearCardNumberTransactionRequest;
import com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionMonthMethod;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionYearMethod;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthMethodResponse;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearMethodResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthYearPaymentMethod;
import com.sanedge.paymentgateway.transaction.service.TransactionMethodByCardService;
import com.sanedge.paymentgateway.transaction.service.TransactionMethodService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionStatsMethodHandler
        extends TransactionStatsMethodServiceGrpc.TransactionStatsMethodServiceImplBase {

    private final TransactionMethodService transactionMethodService;
    private final TransactionMethodByCardService transactionMethodByCardService;

    @Override
    public void findMonthlyPaymentMethods(FindYearTransactionStatus request,
            StreamObserver<ApiResponseTransactionMonthMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse>> res = transactionMethodService
                    .findMonthlyMethod((long) request.getYear());

            ApiResponseTransactionMonthMethod.Builder builder = ApiResponseTransactionMonthMethod.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthMethod(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyPaymentMethods(FindYearTransactionStatus request,
            StreamObserver<ApiResponseTransactionYearMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse>> res = transactionMethodService
                    .findYearlyMethod((long) request.getYear());

            ApiResponseTransactionYearMethod.Builder builder = ApiResponseTransactionYearMethod.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearMethod(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyPaymentMethodsByCardNumber(FindByYearCardNumberTransactionRequest request,
            StreamObserver<ApiResponseTransactionMonthMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse>> res = transactionMethodByCardService
                    .findMonthlyMethod(toMonthYear(request));

            ApiResponseTransactionMonthMethod.Builder builder = ApiResponseTransactionMonthMethod.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthMethod(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyPaymentMethodsByCardNumber(FindByYearCardNumberTransactionRequest request,
            StreamObserver<ApiResponseTransactionYearMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse>> res = transactionMethodByCardService
                    .findYearlyMethod(toMonthYear(request));

            ApiResponseTransactionYearMethod.Builder builder = ApiResponseTransactionYearMethod.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearMethod(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthYearPaymentMethod toMonthYear(FindByYearCardNumberTransactionRequest request) {
        MonthYearPaymentMethod req = new MonthYearPaymentMethod();
        req.setCardNumber(request.getCardNumber());
        req.setYear((long) request.getYear());
        return req;
    }

    private TransactionMonthMethodResponse toMonthMethod(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthMethodResponse r) {
        return TransactionMonthMethodResponse.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setTotalTransactions(r.getTotalTransactions() == null ? 0 : r.getTotalTransactions().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransactionYearMethodResponse toYearMethod(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearMethodResponse r) {
        return TransactionYearMethodResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setTotalTransactions(r.getTotalTransactions() == null ? 0 : r.getTotalTransactions().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
