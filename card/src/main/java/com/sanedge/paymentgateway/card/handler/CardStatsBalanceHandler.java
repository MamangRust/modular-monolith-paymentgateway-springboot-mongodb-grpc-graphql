package com.sanedge.paymentgateway.card.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.card.stats.ApiResponseMonthlyBalance;
import com.sanedge.paymentgateway.pb.card.stats.ApiResponseYearlyBalance;
import com.sanedge.paymentgateway.pb.card.stats.CardResponseMonthlyBalance;
import com.sanedge.paymentgateway.pb.card.stats.CardResponseYearlyBalance;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsBalanceServiceGrpc;
import com.sanedge.paymentgateway.pb.card.stats.FindYearBalance;
import com.sanedge.paymentgateway.pb.card.stats.FindYearBalanceCardNumber;
import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.service.stats.balance.CardBalanceByCardService;
import com.sanedge.paymentgateway.card.service.stats.balance.CardBalanceService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardStatsBalanceHandler extends CardStatsBalanceServiceGrpc.CardStatsBalanceServiceImplBase {

    private final CardBalanceService cardBalanceService;
    private final CardBalanceByCardService cardBalanceByCardService;

    @Override
    public void findMonthlyBalance(FindYearBalance request, StreamObserver<ApiResponseMonthlyBalance> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseMonthBalance>> res = cardBalanceService
                    .findMonthBalance((long) request.getYear());
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyBalance(FindYearBalance request, StreamObserver<ApiResponseYearlyBalance> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseYearBalance>> res = cardBalanceService
                    .findYearBalance((long) request.getYear());
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyBalanceByCardNumber(FindYearBalanceCardNumber request,
            StreamObserver<ApiResponseMonthlyBalance> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseMonthBalance>> res = cardBalanceByCardService
                    .findMonthBalanceByCard(toRequest(request));
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyBalanceByCardNumber(FindYearBalanceCardNumber request,
            StreamObserver<ApiResponseYearlyBalance> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseYearBalance>> res = cardBalanceByCardService
                    .findYearBalanceByCard(toRequest(request));
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthYearCardNumberCard toRequest(FindYearBalanceCardNumber request) {
        return new MonthYearCardNumberCard(request.getCardNumber(), (long) request.getYear());
    }

    private ApiResponseMonthlyBalance toMonthly(
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseMonthBalance>> res) {
        ApiResponseMonthlyBalance.Builder builder = ApiResponseMonthlyBalance.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            res.getData().forEach(r -> builder.addData(CardResponseMonthlyBalance.newBuilder()
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalBalance(r.getTotalBalance() == null ? 0L : r.getTotalBalance())
                    .build()));
        }
        return builder.build();
    }

    private ApiResponseYearlyBalance toYearly(
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.balance.CardResponseYearBalance>> res) {
        ApiResponseYearlyBalance.Builder builder = ApiResponseYearlyBalance.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            res.getData().forEach(r -> builder.addData(CardResponseYearlyBalance.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalBalance(r.getTotalBalance() == null ? 0L : r.getTotalBalance())
                    .build()));
        }
        return builder.build();
    }
}
