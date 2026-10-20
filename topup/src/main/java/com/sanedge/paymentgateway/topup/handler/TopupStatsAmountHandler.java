package com.sanedge.paymentgateway.topup.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.topup.FindYearTopupCardNumber;
import com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupMonthAmount;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupYearAmount;
import com.sanedge.paymentgateway.pb.topup.stats.TopupMonthAmountResponse;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupYearlyAmountResponse;
import com.sanedge.paymentgateway.topup.domain.requests.stats.YearMonthMethod;
import com.sanedge.paymentgateway.topup.service.stats.amount.TopupAmountByCardService;
import com.sanedge.paymentgateway.topup.service.stats.amount.TopupAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopupStatsAmountHandler extends TopupStatsAmountServiceGrpc.TopupStatsAmountServiceImplBase {

    private final TopupAmountService topupAmountService;
    private final TopupAmountByCardService topupAmountByCardService;

    @Override
    public void findMonthlyTopupAmounts(FindYearTopupStatus request,
            StreamObserver<ApiResponseTopupMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupMonthAmountResponse>> res = topupAmountService
                    .findMonthlyAmounts((long) request.getYear());

            observer.onNext(toMonthAmount(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupAmounts(FindYearTopupStatus request, StreamObserver<ApiResponseTopupYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupYearlyAmountResponse>> res = topupAmountService
                    .findYearlyAmounts((long) request.getYear());

            observer.onNext(toYearAmount(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTopupAmountsByCardNumber(FindYearTopupCardNumber request,
            StreamObserver<ApiResponseTopupMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupMonthAmountResponse>> res = topupAmountByCardService
                    .findMonthlyAmounts(toYearMonthMethod(request));

            observer.onNext(toMonthAmount(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupAmountsByCardNumber(FindYearTopupCardNumber request,
            StreamObserver<ApiResponseTopupYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupYearlyAmountResponse>> res = topupAmountByCardService
                    .findYearlyAmounts(toYearMonthMethod(request));

            observer.onNext(toYearAmount(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private YearMonthMethod toYearMonthMethod(FindYearTopupCardNumber request) {
        YearMonthMethod req = new YearMonthMethod();
        req.setCardNumber(request.getCardNumber());
        req.setYear((long) request.getYear());
        return req;
    }

    private ApiResponseTopupMonthAmount toMonthAmount(
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupMonthAmountResponse>> res) {
        ApiResponseTopupMonthAmount.Builder builder = ApiResponseTopupMonthAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(TopupMonthAmountResponse.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build()));
        return builder.build();
    }

    private ApiResponseTopupYearAmount toYearAmount(
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.amount.TopupYearlyAmountResponse>> res) {
        ApiResponseTopupYearAmount.Builder builder = ApiResponseTopupYearAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(TopupYearlyAmountResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build()));
        return builder.build();
    }
}
