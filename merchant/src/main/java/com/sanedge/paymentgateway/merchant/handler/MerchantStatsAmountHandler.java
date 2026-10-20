package com.sanedge.paymentgateway.merchant.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearAmountApiKey;
import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearAmountMerchant;
import com.sanedge.paymentgateway.merchant.service.stats.amount.MerchantAmountByApiKeyService;
import com.sanedge.paymentgateway.merchant.service.stats.amount.MerchantAmountByIdService;
import com.sanedge.paymentgateway.merchant.service.stats.amount.MerchantAmountService;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchant;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById;
import com.sanedge.paymentgateway.pb.merchant.stats.ApiResponseMerchantMonthlyAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.ApiResponseMerchantYearlyAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsAmountServiceGrpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantStatsAmountHandler extends MerchantStatsAmountServiceGrpc.MerchantStatsAmountServiceImplBase {

    private final MerchantAmountService merchantAmountService;
    private final MerchantAmountByIdService merchantAmountByIdService;
    private final MerchantAmountByApiKeyService merchantAmountByApiKeyService;

    @Override
    public void findMonthlyAmountMerchant(FindYearMerchant request,
            StreamObserver<ApiResponseMerchantMonthlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount>> res = merchantAmountService
                    .findMonthAmount((long) request.getYear());
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyAmountMerchant(FindYearMerchant request,
            StreamObserver<ApiResponseMerchantYearlyAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount>> res = merchantAmountService
                    .findYearAmount((long) request.getYear());
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyAmountByMerchants(FindYearMerchantById request,
            StreamObserver<ApiResponseMerchantMonthlyAmount> observer) {
        try {
            MonthYearAmountMerchant req = new MonthYearAmountMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount>> res = merchantAmountByIdService
                    .findMonthAmountById(req);
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyAmountByMerchants(FindYearMerchantById request,
            StreamObserver<ApiResponseMerchantYearlyAmount> observer) {
        try {
            MonthYearAmountMerchant req = new MonthYearAmountMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount>> res = merchantAmountByIdService
                    .findYearAmountById(req);
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyAmountByApikey(FindYearMerchantByApikey request,
            StreamObserver<ApiResponseMerchantMonthlyAmount> observer) {
        try {
            MonthYearAmountApiKey req = new MonthYearAmountApiKey();
            req.setApiKey(request.getApiKey());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount>> res = merchantAmountByApiKeyService
                    .findMonthAmountByApiKey(req);
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyAmountByApikey(FindYearMerchantByApikey request,
            StreamObserver<ApiResponseMerchantYearlyAmount> observer) {
        try {
            MonthYearAmountApiKey req = new MonthYearAmountApiKey();
            req.setApiKey(request.getApiKey());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount>> res = merchantAmountByApiKeyService
                    .findYearAmountByApiKey(req);
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseMerchantMonthlyAmount toMonthly(
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseMonthlyAmount>> res) {
        ApiResponseMerchantMonthlyAmount.Builder builder = ApiResponseMerchantMonthlyAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(MerchantResponseMonthlyAmount.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                .build()));
        return builder.build();
    }

    private ApiResponseMerchantYearlyAmount toYearly(
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.amount.MerchantResponseYearlyAmount>> res) {
        ApiResponseMerchantYearlyAmount.Builder builder = ApiResponseMerchantYearlyAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(MerchantResponseYearlyAmount.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                .build()));
        return builder.build();
    }
}
