package com.sanedge.paymentgateway.withdraw.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.withdraw.FindMonthlyWithdrawStatus;
import com.sanedge.paymentgateway.pb.withdraw.FindMonthlyWithdrawStatusCardNumber;
import com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatus;
import com.sanedge.paymentgateway.pb.withdraw.FindYearWithdrawStatusCardNumber;
import com.sanedge.paymentgateway.pb.withdraw.stats.ApiResponseWithdrawMonthStatusFailed;
import com.sanedge.paymentgateway.pb.withdraw.stats.ApiResponseWithdrawMonthStatusSuccess;
import com.sanedge.paymentgateway.pb.withdraw.stats.ApiResponseWithdrawYearStatusFailed;
import com.sanedge.paymentgateway.pb.withdraw.stats.ApiResponseWithdrawYearStatusSuccess;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthStatusFailedResponse;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawMonthStatusSuccessResponse;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearStatusFailedResponse;
import com.sanedge.paymentgateway.pb.withdraw.stats.WithdrawYearStatusSuccessResponse;
import com.sanedge.paymentgateway.withdraw.domain.requests.MonthStatusWithdraw;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.MonthStatusWithdrawCardNumber;
import com.sanedge.paymentgateway.withdraw.domain.requests.statsbycard.YearStatusWithdrawCardNumber;
import com.sanedge.paymentgateway.withdraw.service.stats.status.WithdrawStatusByCardService;
import com.sanedge.paymentgateway.withdraw.service.stats.status.WithdrawStatusService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawStatsStatusHandler extends WithdrawStatsStatusServiceGrpc.WithdrawStatsStatusServiceImplBase {

    private final WithdrawStatusService withdrawStatusService;
    private final WithdrawStatusByCardService withdrawStatusByCardService;

    @Override
    public void findMonthlyWithdrawStatusSuccess(FindMonthlyWithdrawStatus request,
            StreamObserver<ApiResponseWithdrawMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess>> res = withdrawStatusService
                    .findMonthStatusSuccess(toMonthStatus(request));

            ApiResponseWithdrawMonthStatusSuccess.Builder builder = ApiResponseWithdrawMonthStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonthSuccess(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawStatusSuccess(FindYearWithdrawStatus request,
            StreamObserver<ApiResponseWithdrawYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess>> res = withdrawStatusService
                    .findYearlyStatusSuccess((long) request.getYear());

            ApiResponseWithdrawYearStatusSuccess.Builder builder = ApiResponseWithdrawYearStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYearSuccess(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyWithdrawStatusFailed(FindMonthlyWithdrawStatus request,
            StreamObserver<ApiResponseWithdrawMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed>> res = withdrawStatusService
                    .findMonthStatusFailed(toMonthStatus(request));

            ApiResponseWithdrawMonthStatusFailed.Builder builder = ApiResponseWithdrawMonthStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonthFailed(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawStatusFailed(FindYearWithdrawStatus request,
            StreamObserver<ApiResponseWithdrawYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed>> res = withdrawStatusService
                    .findYearlyStatusFailed((long) request.getYear());

            ApiResponseWithdrawYearStatusFailed.Builder builder = ApiResponseWithdrawYearStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYearFailed(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyWithdrawStatusSuccessCardNumber(FindMonthlyWithdrawStatusCardNumber request,
            StreamObserver<ApiResponseWithdrawMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess>> res = withdrawStatusByCardService
                    .findMonthStatusSuccessByCard(toMonthStatusCardNumber(request));

            ApiResponseWithdrawMonthStatusSuccess.Builder builder = ApiResponseWithdrawMonthStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonthSuccess(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawStatusSuccessCardNumber(FindYearWithdrawStatusCardNumber request,
            StreamObserver<ApiResponseWithdrawYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess>> res = withdrawStatusByCardService
                    .findYearlyStatusSuccessByCard(toYearStatusCardNumber(request));

            ApiResponseWithdrawYearStatusSuccess.Builder builder = ApiResponseWithdrawYearStatusSuccess.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYearSuccess(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyWithdrawStatusFailedCardNumber(FindMonthlyWithdrawStatusCardNumber request,
            StreamObserver<ApiResponseWithdrawMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed>> res = withdrawStatusByCardService
                    .findMonthStatusFailedByCard(toMonthStatusCardNumber(request));

            ApiResponseWithdrawMonthStatusFailed.Builder builder = ApiResponseWithdrawMonthStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonthFailed(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyWithdrawStatusFailedCardNumber(FindYearWithdrawStatusCardNumber request,
            StreamObserver<ApiResponseWithdrawYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed>> res = withdrawStatusByCardService
                    .findYearlyStatusFailedByCard(toYearStatusCardNumber(request));

            ApiResponseWithdrawYearStatusFailed.Builder builder = ApiResponseWithdrawYearStatusFailed.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYearFailed(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthStatusWithdraw toMonthStatus(FindMonthlyWithdrawStatus request) {
        MonthStatusWithdraw req = new MonthStatusWithdraw();
        req.setYear((long) request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private MonthStatusWithdrawCardNumber toMonthStatusCardNumber(FindMonthlyWithdrawStatusCardNumber request) {
        MonthStatusWithdrawCardNumber req = new MonthStatusWithdrawCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear(request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private YearStatusWithdrawCardNumber toYearStatusCardNumber(FindYearWithdrawStatusCardNumber request) {
        YearStatusWithdrawCardNumber req = new YearStatusWithdrawCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear(request.getYear());
        return req;
    }

    private WithdrawMonthStatusSuccessResponse toProtoMonthSuccess(
            com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusSuccess r) {
        return WithdrawMonthStatusSuccessResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private WithdrawYearStatusSuccessResponse toProtoYearSuccess(
            com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusSuccess r) {
        return WithdrawYearStatusSuccessResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private WithdrawMonthStatusFailedResponse toProtoMonthFailed(
            com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseMonthStatusFailed r) {
        return WithdrawMonthStatusFailedResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private WithdrawYearStatusFailedResponse toProtoYearFailed(
            com.sanedge.paymentgateway.withdraw.domain.responses.stats.status.WithdrawResponseYearStatusFailed r) {
        return WithdrawYearStatusFailedResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
