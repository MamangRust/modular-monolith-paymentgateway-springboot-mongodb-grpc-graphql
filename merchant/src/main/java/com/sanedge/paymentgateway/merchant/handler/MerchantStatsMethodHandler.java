package com.sanedge.paymentgateway.merchant.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearPaymentMethodApiKey;
import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearPaymentMethodMerchant;
import com.sanedge.paymentgateway.merchant.service.stats.method.MerchantMethodByApiKeyService;
import com.sanedge.paymentgateway.merchant.service.stats.method.MerchantMethodByIdService;
import com.sanedge.paymentgateway.merchant.service.stats.method.MerchantMethodService;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchant;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById;
import com.sanedge.paymentgateway.pb.merchant.stats.ApiResponseMerchantMonthlyPaymentMethod;
import com.sanedge.paymentgateway.pb.merchant.stats.ApiResponseMerchantYearlyPaymentMethod;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyPaymentMethod;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsMethodServiceGrpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantStatsMethodHandler extends MerchantStatsMethodServiceGrpc.MerchantStatsMethodServiceImplBase {

    private final MerchantMethodService merchantMethodService;
    private final MerchantMethodByIdService merchantMethodByIdService;
    private final MerchantMethodByApiKeyService merchantMethodByApiKeyService;

    @Override
    public void findMonthlyPaymentMethodsMerchant(FindYearMerchant request,
            StreamObserver<ApiResponseMerchantMonthlyPaymentMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod>> res = merchantMethodService
                    .findMonthMethod((long) request.getYear());
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyPaymentMethodMerchant(FindYearMerchant request,
            StreamObserver<ApiResponseMerchantYearlyPaymentMethod> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod>> res = merchantMethodService
                    .findYearMethod((long) request.getYear());
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyPaymentMethodByMerchants(FindYearMerchantById request,
            StreamObserver<ApiResponseMerchantMonthlyPaymentMethod> observer) {
        try {
            MonthYearPaymentMethodMerchant req = new MonthYearPaymentMethodMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod>> res = merchantMethodByIdService
                    .findMonthMethodById(req);
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyPaymentMethodByMerchants(FindYearMerchantById request,
            StreamObserver<ApiResponseMerchantYearlyPaymentMethod> observer) {
        try {
            MonthYearPaymentMethodMerchant req = new MonthYearPaymentMethodMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod>> res = merchantMethodByIdService
                    .findYearMethodById(req);
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyPaymentMethodByApikey(FindYearMerchantByApikey request,
            StreamObserver<ApiResponseMerchantMonthlyPaymentMethod> observer) {
        try {
            MonthYearPaymentMethodApiKey req = new MonthYearPaymentMethodApiKey();
            req.setApiKey(request.getApiKey());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod>> res = merchantMethodByApiKeyService
                    .findMonthMethod(req);
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyPaymentMethodByApikey(FindYearMerchantByApikey request,
            StreamObserver<ApiResponseMerchantYearlyPaymentMethod> observer) {
        try {
            MonthYearPaymentMethodApiKey req = new MonthYearPaymentMethodApiKey();
            req.setApiKey(request.getApiKey());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod>> res = merchantMethodByApiKeyService
                    .findYearMethod(req);
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseMerchantMonthlyPaymentMethod toMonthly(
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseMonthlyPaymentMethod>> res) {
        ApiResponseMerchantMonthlyPaymentMethod.Builder builder = ApiResponseMerchantMonthlyPaymentMethod.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(MerchantResponseMonthlyPaymentMethod.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                .build()));
        return builder.build();
    }

    private ApiResponseMerchantYearlyPaymentMethod toYearly(
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.method.MerchantResponseYearlyPaymentMethod>> res) {
        ApiResponseMerchantYearlyPaymentMethod.Builder builder = ApiResponseMerchantYearlyPaymentMethod.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(MerchantResponseYearlyPaymentMethod.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setPaymentMethod(ProtoMappers.nvl(r.getPaymentMethod()))
                .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                .build()));
        return builder.build();
    }
}
