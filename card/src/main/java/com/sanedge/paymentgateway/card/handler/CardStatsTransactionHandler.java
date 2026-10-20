package com.sanedge.paymentgateway.card.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.card.ApiResponseMonthlyAmount;
import com.sanedge.paymentgateway.pb.card.ApiResponseYearlyAmount;
import com.sanedge.paymentgateway.pb.card.CardResponseMonthlyAmount;
import com.sanedge.paymentgateway.pb.card.CardResponseYearlyAmount;
import com.sanedge.paymentgateway.pb.card.FindYearAmount;
import com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTransactionServiceGrpc;
import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.service.stats.transaction.CardTransactionAmountByCardService;
import com.sanedge.paymentgateway.card.service.stats.transaction.CardTransactionAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardStatsTransactionHandler extends CardStatsTransactionServiceGrpc.CardStatsTransactionServiceImplBase {

    private final CardTransactionAmountService cardTransactionAmountService;
    private final CardTransactionAmountByCardService cardTransactionAmountByCardService;

    @Override
    public void findMonthlyTransactionAmount(FindYearAmount request, StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount>> res = cardTransactionAmountService
                    .findMonthAmount((long) request.getYear());
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransactionAmount(FindYearAmount request, StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount>> res = cardTransactionAmountService
                    .findYearAmount((long) request.getYear());
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransactionAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount>> res = cardTransactionAmountByCardService
                    .findMonthAmountByCard(toRequest(request));
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransactionAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount>> res = cardTransactionAmountByCardService
                    .findYearAmountByCard(toRequest(request));
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthYearCardNumberCard toRequest(FindYearAmountCardNumber request) {
        return new MonthYearCardNumberCard(request.getCardNumber(), (long) request.getYear());
    }

    private ApiResponseMonthlyAmount toMonthly(
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount>> res) {
        ApiResponseMonthlyAmount.Builder builder = ApiResponseMonthlyAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            res.getData().forEach(r -> builder.addData(CardResponseMonthlyAmount.newBuilder()
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                    .build()));
        }
        return builder.build();
    }

    private ApiResponseYearlyAmount toYearly(
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount>> res) {
        ApiResponseYearlyAmount.Builder builder = ApiResponseYearlyAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            res.getData().forEach(r -> builder.addData(CardResponseYearlyAmount.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                    .build()));
        }
        return builder.build();
    }
}
