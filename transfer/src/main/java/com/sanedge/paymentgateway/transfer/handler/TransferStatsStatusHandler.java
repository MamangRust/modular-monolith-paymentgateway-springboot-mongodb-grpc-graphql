package com.sanedge.paymentgateway.transfer.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.transfer.FindMonthlyTransferStatus;
import com.sanedge.paymentgateway.pb.transfer.FindMonthlyTransferStatusCardNumber;
import com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatus;
import com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatusCardNumber;
import com.sanedge.paymentgateway.pb.transfer.stats.ApiResponseTransferMonthStatusFailed;
import com.sanedge.paymentgateway.pb.transfer.stats.ApiResponseTransferMonthStatusSuccess;
import com.sanedge.paymentgateway.pb.transfer.stats.ApiResponseTransferYearStatusFailed;
import com.sanedge.paymentgateway.pb.transfer.stats.ApiResponseTransferYearStatusSuccess;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthStatusFailedResponse;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthStatusSuccessResponse;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferStatsStatusServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferYearStatusFailedResponse;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferYearStatusSuccessResponse;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthStatusTransfer;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthStatusTransferCardNumber;
import com.sanedge.paymentgateway.transfer.domain.requests.YearStatusTransferCardNumber;
import com.sanedge.paymentgateway.transfer.service.TransferStatusByCardService;
import com.sanedge.paymentgateway.transfer.service.TransferStatusService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferStatsStatusHandler extends TransferStatsStatusServiceGrpc.TransferStatsStatusServiceImplBase {

    private final TransferStatusService transferStatusService;
    private final TransferStatusByCardService transferStatusByCardService;

    @Override
    public void findMonthlyTransferStatusSuccess(FindMonthlyTransferStatus request,
            StreamObserver<ApiResponseTransferMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess>> res = transferStatusService
                    .findMonthStatusSuccess(toMonthStatusTransfer(request));

            ApiResponseTransferMonthStatusSuccess.Builder builder = ApiResponseTransferMonthStatusSuccess.newBuilder()
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
    public void findYearlyTransferStatusSuccess(FindYearTransferStatus request,
            StreamObserver<ApiResponseTransferYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess>> res = transferStatusService
                    .findYearlyStatusSuccess((long) request.getYear());

            ApiResponseTransferYearStatusSuccess.Builder builder = ApiResponseTransferYearStatusSuccess.newBuilder()
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
    public void findMonthlyTransferStatusFailed(FindMonthlyTransferStatus request,
            StreamObserver<ApiResponseTransferMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed>> res = transferStatusService
                    .findMonthStatusFailed(toMonthStatusTransfer(request));

            ApiResponseTransferMonthStatusFailed.Builder builder = ApiResponseTransferMonthStatusFailed.newBuilder()
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
    public void findYearlyTransferStatusFailed(FindYearTransferStatus request,
            StreamObserver<ApiResponseTransferYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed>> res = transferStatusService
                    .findYearlyStatusFailed((long) request.getYear());

            ApiResponseTransferYearStatusFailed.Builder builder = ApiResponseTransferYearStatusFailed.newBuilder()
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
    public void findMonthlyTransferStatusSuccessByCardNumber(FindMonthlyTransferStatusCardNumber request,
            StreamObserver<ApiResponseTransferMonthStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess>> res = transferStatusByCardService
                    .findMonthStatusSuccessByCard(toMonthStatusTransferCardNumber(request));

            ApiResponseTransferMonthStatusSuccess.Builder builder = ApiResponseTransferMonthStatusSuccess.newBuilder()
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
    public void findYearlyTransferStatusSuccessByCardNumber(FindYearTransferStatusCardNumber request,
            StreamObserver<ApiResponseTransferYearStatusSuccess> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess>> res = transferStatusByCardService
                    .findYearlyStatusSuccessByCard(toYearStatusTransferCardNumber(request));

            ApiResponseTransferYearStatusSuccess.Builder builder = ApiResponseTransferYearStatusSuccess.newBuilder()
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
    public void findMonthlyTransferStatusFailedByCardNumber(FindMonthlyTransferStatusCardNumber request,
            StreamObserver<ApiResponseTransferMonthStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed>> res = transferStatusByCardService
                    .findMonthStatusFailedByCard(toMonthStatusTransferCardNumber(request));

            ApiResponseTransferMonthStatusFailed.Builder builder = ApiResponseTransferMonthStatusFailed.newBuilder()
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
    public void findYearlyTransferStatusFailedByCardNumber(FindYearTransferStatusCardNumber request,
            StreamObserver<ApiResponseTransferYearStatusFailed> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed>> res = transferStatusByCardService
                    .findYearlyStatusFailedByCard(toYearStatusTransferCardNumber(request));

            ApiResponseTransferYearStatusFailed.Builder builder = ApiResponseTransferYearStatusFailed.newBuilder()
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

    private MonthStatusTransfer toMonthStatusTransfer(FindMonthlyTransferStatus request) {
        MonthStatusTransfer req = new MonthStatusTransfer();
        req.setYear(request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private MonthStatusTransferCardNumber toMonthStatusTransferCardNumber(FindMonthlyTransferStatusCardNumber request) {
        MonthStatusTransferCardNumber req = new MonthStatusTransferCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear(request.getYear());
        req.setMonth(request.getMonth());
        return req;
    }

    private YearStatusTransferCardNumber toYearStatusTransferCardNumber(FindYearTransferStatusCardNumber request) {
        YearStatusTransferCardNumber req = new YearStatusTransferCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear(request.getYear());
        return req;
    }

    private TransferMonthStatusSuccessResponse toProtoMonthSuccess(
            com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusSuccess r) {
        return TransferMonthStatusSuccessResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransferYearStatusSuccessResponse toProtoYearSuccess(
            com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusSuccess r) {
        return TransferYearStatusSuccessResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalSuccess(r.getTotalSuccess() == null ? 0 : r.getTotalSuccess().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransferMonthStatusFailedResponse toProtoMonthFailed(
            com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseMonthStatusFailed r) {
        return TransferMonthStatusFailedResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransferYearStatusFailedResponse toProtoYearFailed(
            com.sanedge.paymentgateway.transfer.domain.responses.TransferResponseYearStatusFailed r) {
        return TransferYearStatusFailedResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalFailed(r.getTotalFailed() == null ? 0 : r.getTotalFailed().intValue())
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
