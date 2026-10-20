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
import com.sanedge.paymentgateway.pb.card.stats.CardStatsWithdrawServiceGrpc;
import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.service.stats.withdraw.CardWithdrawAmountByCardService;
import com.sanedge.paymentgateway.card.service.stats.withdraw.CardWithdrawAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardStatsWithdrawHandler extends CardStatsWithdrawServiceGrpc.CardStatsWithdrawServiceImplBase {

    private final CardWithdrawAmountService cardWithdrawAmountService;
    private final CardWithdrawAmountByCardService cardWithdrawAmountByCardService;

    @Override
    public void findMonthlyWithdrawAmount(FindYearAmount request, StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount>> res = cardWithdrawAmountService
                    .findMonthAmount((long) request.getYear());
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawAmount(FindYearAmount request, StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount>> res = cardWithdrawAmountService
                    .findYearAmount((long) request.getYear());
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyWithdrawAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount>> res = cardWithdrawAmountByCardService
                    .findMonthAmountByCard(toRequest(request));
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount>> res = cardWithdrawAmountByCardService
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
