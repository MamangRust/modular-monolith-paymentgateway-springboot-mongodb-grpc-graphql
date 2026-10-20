package com.sanedge.paymentgateway.topup.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.topup.FindYearTopupCardNumber;
import com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupMonthMethod;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupYearMethod;
import com.sanedge.paymentgateway.pb.topup.stats.TopupMonthMethodResponse;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsMethodServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupYearlyMethodResponse;
import com.sanedge.paymentgateway.topup.domain.requests.stats.YearMonthMethod;
import com.sanedge.paymentgateway.topup.service.stats.method.TopupMethodByCardService;
import com.sanedge.paymentgateway.topup.service.stats.method.TopupMethodService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopupStatsMethodHandler extends TopupStatsMethodServiceGrpc.TopupStatsMethodServiceImplBase {

    private final TopupMethodService topupMethodService;
    private final TopupMethodByCardService topupMethodByCardService;

    @Override
    public void findMonthlyTopupMethods(FindYearTopupStatus request,
            StreamObserver<ApiResponseTopupMonthMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupMonthMethodResponse>> res = topupMethodService
                    .findMonthlyMethods((long) request.getYear());

            observer.onNext(toMonthMethod(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupMethods(FindYearTopupStatus request,
            StreamObserver<ApiResponseTopupYearMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupYearlyMethodResponse>> res = topupMethodService
                    .findYearlyMethods((long) request.getYear());

            observer.onNext(toYearMethod(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTopupMethodsByCardNumber(FindYearTopupCardNumber request,
            StreamObserver<ApiResponseTopupMonthMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupMonthMethodResponse>> res = topupMethodByCardService
                    .findMonthlyMethods(toYearMonthMethod(request));

            observer.onNext(toMonthMethod(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupMethodsByCardNumber(FindYearTopupCardNumber request,
            StreamObserver<ApiResponseTopupYearMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupYearlyMethodResponse>> res = topupMethodByCardService
                    .findYearlyMethods(toYearMonthMethod(request));

            observer.onNext(toYearMethod(res));
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

    private ApiResponseTopupMonthMethod toMonthMethod(
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupMonthMethodResponse>> res) {
        ApiResponseTopupMonthMethod.Builder builder = ApiResponseTopupMonthMethod.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(TopupMonthMethodResponse.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTopupMethod(ProtoMappers.nvl(r.getTopupMethod()))
                .setTotalTopups(r.getTotalTopups() == null ? 0 : r.getTotalTopups().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build()));
        return builder.build();
    }

    private ApiResponseTopupYearMethod toYearMethod(
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.method.TopupYearlyMethodResponse>> res) {
        ApiResponseTopupYearMethod.Builder builder = ApiResponseTopupYearMethod.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(TopupYearlyMethodResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTopupMethod(ProtoMappers.nvl(r.getTopupMethod()))
                .setTotalTopups(r.getTotalTopups() == null ? 0 : r.getTotalTopups().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build()));
        return builder.build();
    }
}
