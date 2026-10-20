package com.sanedge.paymentgateway.transaction.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.transaction.FindByYearCardNumberTransactionRequest;
import com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionMonthAmount;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionYearAmount;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthAmountResponse;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearlyAmountResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthYearPaymentMethod;
import com.sanedge.paymentgateway.transaction.service.TransactionAmountByCardService;
import com.sanedge.paymentgateway.transaction.service.TransactionAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionStatsAmountHandler
        extends TransactionStatsAmountServiceGrpc.TransactionStatsAmountServiceImplBase {

    private final TransactionAmountService transactionAmountService;
    private final TransactionAmountByCardService transactionAmountByCardService;

    @Override
    public void findMonthlyAmounts(FindYearTransactionStatus request,
            StreamObserver<ApiResponseTransactionMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthAmountResponse>> res = transactionAmountService
                    .findMonthlyAmounts((long) request.getYear());

            ApiResponseTransactionMonthAmount.Builder builder = ApiResponseTransactionMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthAmount(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyAmounts(FindYearTransactionStatus request,
            StreamObserver<ApiResponseTransactionYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearlyAmountResponse>> res = transactionAmountService
                    .findYearlyAmounts((long) request.getYear());

            ApiResponseTransactionYearAmount.Builder builder = ApiResponseTransactionYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearAmount(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyAmountsByCardNumber(FindByYearCardNumberTransactionRequest request,
            StreamObserver<ApiResponseTransactionMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthAmountResponse>> res = transactionAmountByCardService
                    .findMonthlyAmounts(toMonthYear(request));

            ApiResponseTransactionMonthAmount.Builder builder = ApiResponseTransactionMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthAmount(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyAmountsByCardNumber(FindByYearCardNumberTransactionRequest request,
            StreamObserver<ApiResponseTransactionYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearlyAmountResponse>> res = transactionAmountByCardService
                    .findYearlyAmounts(toMonthYear(request));

            ApiResponseTransactionYearAmount.Builder builder = ApiResponseTransactionYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearAmount(r)));

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

    private TransactionMonthAmountResponse toMonthAmount(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionMonthAmountResponse r) {
        return TransactionMonthAmountResponse.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransactionYearlyAmountResponse toYearAmount(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionYearlyAmountResponse r) {
        return TransactionYearlyAmountResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
