package com.sanedge.paymentgateway.transaction.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.transaction.FindMonthlyTransactionStatus;
import com.sanedge.paymentgateway.pb.transaction.FindMonthlyTransactionStatusCardNumber;
import com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatus;
import com.sanedge.paymentgateway.pb.transaction.FindYearTransactionStatusCardNumber;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionMonthStatusFailed;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionMonthStatusSuccess;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionYearStatusFailed;
import com.sanedge.paymentgateway.pb.transaction.stats.ApiResponseTransactionYearStatusSuccess;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthStatusFailedResponse;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionMonthStatusSuccessResponse;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearStatusFailedResponse;
import com.sanedge.paymentgateway.pb.transaction.stats.TransactionYearStatusSuccessResponse;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthStatusTransaction;
import com.sanedge.paymentgateway.transaction.domain.requests.MonthStatusTransactionCardNumber;
import com.sanedge.paymentgateway.transaction.domain.requests.YearStatusTransactionCardNumber;
import com.sanedge.paymentgateway.transaction.service.TransactionStatusByCardService;
import com.sanedge.paymentgateway.transaction.service.TransactionStatusService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionStatsStatusHandler
        extends TransactionStatsStatusServiceGrpc.TransactionStatsStatusServiceImplBase {

    private final TransactionStatusService transactionStatusService;
    private final TransactionStatusByCardService transactionStatusByCardService;

    @Override
    public void findMonthlyTransactionStatusSuccess(FindMonthlyTransactionStatus request,
            StreamObserver<ApiResponseTransactionMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess>> res = transactionStatusService
                    .findMonthStatusSuccess(toMonthStatus(request));

            ApiResponseTransactionMonthStatusSuccess.Builder builder = ApiResponseTransactionMonthStatusSuccess
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthSuccess(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransactionStatusSuccess(FindYearTransactionStatus request,
            StreamObserver<ApiResponseTransactionYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess>> res = transactionStatusService
                    .findYearlyStatusSuccess((long) request.getYear());

            ApiResponseTransactionYearStatusSuccess.Builder builder = ApiResponseTransactionYearStatusSuccess
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearSuccess(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransactionStatusFailed(FindMonthlyTransactionStatus request,
            StreamObserver<ApiResponseTransactionMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed>> res = transactionStatusService
                    .findMonthStatusFailed(toMonthStatus(request));

            ApiResponseTransactionMonthStatusFailed.Builder builder = ApiResponseTransactionMonthStatusFailed
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthFailed(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransactionStatusFailed(FindYearTransactionStatus request,
            StreamObserver<ApiResponseTransactionYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed>> res = transactionStatusService
                    .findYearlyStatusFailed((long) request.getYear());

            ApiResponseTransactionYearStatusFailed.Builder builder = ApiResponseTransactionYearStatusFailed
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearFailed(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransactionStatusSuccessByCardNumber(FindMonthlyTransactionStatusCardNumber request,
            StreamObserver<ApiResponseTransactionMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess>> res = transactionStatusByCardService
                    .findMonthStatusSuccess(toMonthStatusCard(request));

            ApiResponseTransactionMonthStatusSuccess.Builder builder = ApiResponseTransactionMonthStatusSuccess
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthSuccess(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransactionStatusSuccessByCardNumber(FindYearTransactionStatusCardNumber request,
            StreamObserver<ApiResponseTransactionYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess>> res = transactionStatusByCardService
                    .findYearlyStatusSuccess(toYearStatusCard(request));

            ApiResponseTransactionYearStatusSuccess.Builder builder = ApiResponseTransactionYearStatusSuccess
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearSuccess(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransactionStatusFailedByCardNumber(FindMonthlyTransactionStatusCardNumber request,
            StreamObserver<ApiResponseTransactionMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed>> res = transactionStatusByCardService
                    .findMonthStatusFailed(toMonthStatusCard(request));

            ApiResponseTransactionMonthStatusFailed.Builder builder = ApiResponseTransactionMonthStatusFailed
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toMonthFailed(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransactionStatusFailedByCardNumber(FindYearTransactionStatusCardNumber request,
            StreamObserver<ApiResponseTransactionYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed>> res = transactionStatusByCardService
                    .findYearlyStatusFailed(toYearStatusCard(request));

            ApiResponseTransactionYearStatusFailed.Builder builder = ApiResponseTransactionYearStatusFailed
                    .newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(toYearFailed(r)));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthStatusTransaction toMonthStatus(FindMonthlyTransactionStatus request) {
        MonthStatusTransaction req = new MonthStatusTransaction();
        req.setYear((long) request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private MonthStatusTransactionCardNumber toMonthStatusCard(FindMonthlyTransactionStatusCardNumber request) {
        MonthStatusTransactionCardNumber req = new MonthStatusTransactionCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear((long) request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private YearStatusTransactionCardNumber toYearStatusCard(FindYearTransactionStatusCardNumber request) {
        YearStatusTransactionCardNumber req = new YearStatusTransactionCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear((long) request.getYear());
        return req;
    }

    private TransactionMonthStatusSuccessResponse toMonthSuccess(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusSuccess r) {
        return TransactionMonthStatusSuccessResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransactionYearStatusSuccessResponse toYearSuccess(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusSuccess r) {
        return TransactionYearStatusSuccessResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransactionMonthStatusFailedResponse toMonthFailed(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseMonthStatusFailed r) {
        return TransactionMonthStatusFailedResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransactionYearStatusFailedResponse toYearFailed(
            com.sanedge.paymentgateway.transaction.domain.responses.TransactionResponseYearStatusFailed r) {
        return TransactionYearStatusFailedResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
