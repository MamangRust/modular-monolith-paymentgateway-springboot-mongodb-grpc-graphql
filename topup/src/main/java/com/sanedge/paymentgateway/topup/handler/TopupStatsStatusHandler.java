package com.sanedge.paymentgateway.topup.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.topup.FindMonthlyTopupStatus;
import com.sanedge.paymentgateway.pb.topup.FindMonthlyTopupStatusCardNumber;
import com.sanedge.paymentgateway.pb.topup.FindYearTopupStatus;
import com.sanedge.paymentgateway.pb.topup.FindYearTopupStatusCardNumber;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupMonthStatusFailed;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupMonthStatusSuccess;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupYearStatusFailed;
import com.sanedge.paymentgateway.pb.topup.stats.ApiResponseTopupYearStatusSuccess;
import com.sanedge.paymentgateway.pb.topup.stats.TopupMonthStatusFailedResponse;
import com.sanedge.paymentgateway.pb.topup.stats.TopupMonthStatusSuccessResponse;
import com.sanedge.paymentgateway.pb.topup.stats.TopupStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.stats.TopupYearStatusFailedResponse;
import com.sanedge.paymentgateway.pb.topup.stats.TopupYearStatusSuccessResponse;
import com.sanedge.paymentgateway.topup.domain.requests.stats.MonthTopupStatus;
import com.sanedge.paymentgateway.topup.domain.requests.statsbycard.MonthTopupStatusCardNumber;
import com.sanedge.paymentgateway.topup.domain.requests.statsbycard.YearTopupStatusCardNumber;
import com.sanedge.paymentgateway.topup.service.stats.status.TopupStatusByCardService;
import com.sanedge.paymentgateway.topup.service.stats.status.TopupStatusService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopupStatsStatusHandler extends TopupStatsStatusServiceGrpc.TopupStatsStatusServiceImplBase {

    private final TopupStatusService topupStatusService;
    private final TopupStatusByCardService topupStatusByCardService;

    @Override
    public void findMonthlyTopupStatusSuccess(FindMonthlyTopupStatus request,
            StreamObserver<ApiResponseTopupMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusSuccess>> res = topupStatusService
                    .findMonthStatusSuccess(toMonthStatus(request));

            ApiResponseTopupMonthStatusSuccess.Builder builder = ApiResponseTopupMonthStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupMonthStatusSuccessResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupStatusSuccess(FindYearTopupStatus request,
            StreamObserver<ApiResponseTopupYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusSuccess>> res = topupStatusService
                    .findYearlyStatusSuccess((long) request.getYear());

            ApiResponseTopupYearStatusSuccess.Builder builder = ApiResponseTopupYearStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupYearStatusSuccessResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTopupStatusFailed(FindMonthlyTopupStatus request,
            StreamObserver<ApiResponseTopupMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusFailed>> res = topupStatusService
                    .findMonthStatusFailed(toMonthStatus(request));

            ApiResponseTopupMonthStatusFailed.Builder builder = ApiResponseTopupMonthStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupMonthStatusFailedResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupStatusFailed(FindYearTopupStatus request,
            StreamObserver<ApiResponseTopupYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusFailed>> res = topupStatusService
                    .findYearlyStatusFailed((long) request.getYear());

            ApiResponseTopupYearStatusFailed.Builder builder = ApiResponseTopupYearStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupYearStatusFailedResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTopupStatusSuccessByCardNumber(FindMonthlyTopupStatusCardNumber request,
            StreamObserver<ApiResponseTopupMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusSuccess>> res = topupStatusByCardService
                    .findMonthStatusSuccess(toMonthStatusByCard(request));

            ApiResponseTopupMonthStatusSuccess.Builder builder = ApiResponseTopupMonthStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupMonthStatusSuccessResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupStatusSuccessByCardNumber(FindYearTopupStatusCardNumber request,
            StreamObserver<ApiResponseTopupYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusSuccess>> res = topupStatusByCardService
                    .findYearlyStatusSuccess(toYearStatusByCard(request));

            ApiResponseTopupYearStatusSuccess.Builder builder = ApiResponseTopupYearStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupYearStatusSuccessResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTopupStatusFailedByCardNumber(FindMonthlyTopupStatusCardNumber request,
            StreamObserver<ApiResponseTopupMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseMonthStatusFailed>> res = topupStatusByCardService
                    .findMonthStatusFailed(toMonthStatusByCard(request));

            ApiResponseTopupMonthStatusFailed.Builder builder = ApiResponseTopupMonthStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupMonthStatusFailedResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTopupStatusFailedByCardNumber(FindYearTopupStatusCardNumber request,
            StreamObserver<ApiResponseTopupYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.topup.domain.responses.stats.status.TopupResponseYearStatusFailed>> res = topupStatusByCardService
                    .findYearlyStatusFailed(toYearStatusByCard(request));

            ApiResponseTopupYearStatusFailed.Builder builder = ApiResponseTopupYearStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(TopupYearStatusFailedResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                    .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthTopupStatus toMonthStatus(FindMonthlyTopupStatus request) {
        MonthTopupStatus req = new MonthTopupStatus();
        req.setYear((long) request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private MonthTopupStatusCardNumber toMonthStatusByCard(FindMonthlyTopupStatusCardNumber request) {
        MonthTopupStatusCardNumber req = new MonthTopupStatusCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear((long) request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private YearTopupStatusCardNumber toYearStatusByCard(FindYearTopupStatusCardNumber request) {
        YearTopupStatusCardNumber req = new YearTopupStatusCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear((long) request.getYear());
        return req;
    }
}
