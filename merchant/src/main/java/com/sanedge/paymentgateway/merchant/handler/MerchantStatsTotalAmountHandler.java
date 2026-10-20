package com.sanedge.paymentgateway.merchant.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey.MonthYearTotalAmountApiKey;
import com.sanedge.paymentgateway.merchant.domain.requests.statsbyid.MonthYearTotalAmountMerchant;
import com.sanedge.paymentgateway.merchant.service.stats.totalamount.MerchantTotalAmountByApiKeyService;
import com.sanedge.paymentgateway.merchant.service.stats.totalamount.MerchantTotalAmountByIdService;
import com.sanedge.paymentgateway.merchant.service.stats.totalamount.MerchantTotalAmountService;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchant;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchantByApikey;
import com.sanedge.paymentgateway.pb.merchant.FindYearMerchantById;
import com.sanedge.paymentgateway.pb.merchant.stats.ApiResponseMerchantMonthlyTotalAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.ApiResponseMerchantYearlyTotalAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseMonthlyTotalAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantResponseYearlyTotalAmount;
import com.sanedge.paymentgateway.pb.merchant.stats.MerchantStatsTotalAmountServiceGrpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantStatsTotalAmountHandler
        extends MerchantStatsTotalAmountServiceGrpc.MerchantStatsTotalAmountServiceImplBase {

    private final MerchantTotalAmountService merchantTotalAmountService;
    private final MerchantTotalAmountByIdService merchantTotalAmountByIdService;
    private final MerchantTotalAmountByApiKeyService merchantTotalAmountByApiKeyService;

    @Override
    public void findMonthlyTotalAmountMerchant(FindYearMerchant request,
            StreamObserver<ApiResponseMerchantMonthlyTotalAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount>> res = merchantTotalAmountService
                    .findMonthTotalAmount((long) request.getYear());
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTotalAmountMerchant(FindYearMerchant request,
            StreamObserver<ApiResponseMerchantYearlyTotalAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount>> res = merchantTotalAmountService
                    .findYearTotalAmount((long) request.getYear());
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTotalAmountByMerchants(FindYearMerchantById request,
            StreamObserver<ApiResponseMerchantMonthlyTotalAmount> observer) {
        try {
            MonthYearTotalAmountMerchant req = new MonthYearTotalAmountMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount>> res = merchantTotalAmountByIdService
                    .findMonthTotalAmountById(req);
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTotalAmountByMerchants(FindYearMerchantById request,
            StreamObserver<ApiResponseMerchantYearlyTotalAmount> observer) {
        try {
            MonthYearTotalAmountMerchant req = new MonthYearTotalAmountMerchant();
            req.setMerchantId(request.getMerchantId());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount>> res = merchantTotalAmountByIdService
                    .findYearTotalAmountById(req);
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTotalAmountByApikey(FindYearMerchantByApikey request,
            StreamObserver<ApiResponseMerchantMonthlyTotalAmount> observer) {
        try {
            MonthYearTotalAmountApiKey req = new MonthYearTotalAmountApiKey();
            req.setApiKey(request.getApiKey());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount>> res = merchantTotalAmountByApiKeyService
                    .findMonthTotalAmountByApiKey(req);
            observer.onNext(toMonthly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTotalAmountByApikey(FindYearMerchantByApikey request,
            StreamObserver<ApiResponseMerchantYearlyTotalAmount> observer) {
        try {
            MonthYearTotalAmountApiKey req = new MonthYearTotalAmountApiKey();
            req.setApiKey(request.getApiKey());
            req.setYear((long) request.getYear());

            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount>> res = merchantTotalAmountByApiKeyService
                    .findYearTotalAmountByApiKey(req);
            observer.onNext(toYearly(res));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private ApiResponseMerchantMonthlyTotalAmount toMonthly(
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseMonthlyTotalAmount>> res) {
        ApiResponseMerchantMonthlyTotalAmount.Builder builder = ApiResponseMerchantMonthlyTotalAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(MerchantResponseMonthlyTotalAmount.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                .build()));
        return builder.build();
    }

    private ApiResponseMerchantYearlyTotalAmount toYearly(
            ApiResponse<List<com.sanedge.paymentgateway.merchant.domain.responses.stats.total_amount.MerchantResponseYearlyTotalAmount>> res) {
        ApiResponseMerchantYearlyTotalAmount.Builder builder = ApiResponseMerchantYearlyTotalAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        res.getData().forEach(r -> builder.addData(MerchantResponseYearlyTotalAmount.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                .build()));
        return builder.build();
    }
}
