package com.sanedge.paymentgateway.withdraw.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawCardNumber;
import com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatus;
import com.sanedge.paymentgateway.pb.withdraw.stats.ApiResponseWithdrawMonthAmount;
import com.sanedge.paymentgateway.pb.withdraw.stats.ApiResponseWithdrawYearAmount;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthlyAmountResponse;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearlyAmountResponse;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.YearMonthCardNumber;
import com.sanedge.paymentgateway.withdraw.service.stats.amount.WithdrawAmountByCardService;
import com.sanedge.paymentgateway.withdraw.service.stats.amount.WithdrawAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawStatsAmountHandler extends WithdrawStatsAmountServiceGrpc.WithdrawStatsAmountServiceImplBase {

    private final WithdrawAmountService withdrawAmountService;
    private final WithdrawAmountByCardService withdrawAmountByCardService;

    @Override
    public void findMonthlyWithdraws(FindYearWithdrawStatus request,
            StreamObserver<ApiResponseWithdrawMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawMonthlyAmountResponse>> res = withdrawAmountService
                    .findMonthlyWithdraws((long) request.getYear());

            ApiResponseWithdrawMonthAmount.Builder builder = ApiResponseWithdrawMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonthly(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdraws(FindYearWithdrawStatus request,
            StreamObserver<ApiResponseWithdrawYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawYearlyAmountResponse>> res = withdrawAmountService
                    .findYearlyWithdraws((long) request.getYear());

            ApiResponseWithdrawYearAmount.Builder builder = ApiResponseWithdrawYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYearly(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyWithdrawsByCardNumber(FindYearWithdrawCardNumber request,
            StreamObserver<ApiResponseWithdrawMonthAmount> observer) {
        try {
            YearMonthCardNumber req = new YearMonthCardNumber();
            req.setCardNumber(request.getCardNumber());
            req.setYear(request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawMonthlyAmountResponse>> res = withdrawAmountByCardService
                    .findMonthlyByCardNumber(req);

            ApiResponseWithdrawMonthAmount.Builder builder = ApiResponseWithdrawMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonthly(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawsByCardNumber(FindYearWithdrawCardNumber request,
            StreamObserver<ApiResponseWithdrawYearAmount> observer) {
        try {
            YearMonthCardNumber req = new YearMonthCardNumber();
            req.setCardNumber(request.getCardNumber());
            req.setYear(request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawYearlyAmountResponse>> res = withdrawAmountByCardService
                    .findYearlyByCardNumber(req);

            ApiResponseWithdrawYearAmount.Builder builder = ApiResponseWithdrawYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYearly(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private WithdrawMonthlyAmountResponse toProtoMonthly(
            com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawMonthlyAmountResponse r) {
        return WithdrawMonthlyAmountResponse.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private WithdrawYearlyAmountResponse toProtoYearly(
            com.sanedge.paymentgateway.withdraw.domain.responses.stats.amount.WithdrawYearlyAmountResponse r) {
        return WithdrawYearlyAmountResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
