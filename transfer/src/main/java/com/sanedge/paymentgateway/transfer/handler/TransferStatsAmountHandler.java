package com.sanedge.paymentgateway.transfer.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.transfer.FindByCardNumberTransferRequest;
import com.sanedge.paymentgateway.pb.transfer.FindYearTransferStatus;
import com.sanedge.paymentgateway.pb.transfer.stats.ApiResponseTransferMonthAmount;
import com.sanedge.paymentgateway.pb.transfer.stats.ApiResponseTransferYearAmount;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferMonthAmountResponse;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferStatsAmountServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.stats.TransferYearAmountResponse;
import com.sanedge.paymentgateway.transfer.domain.requests.MonthYearCardNumber;
import com.sanedge.paymentgateway.transfer.service.TransferAmountByCardService;
import com.sanedge.paymentgateway.transfer.service.TransferAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferStatsAmountHandler extends TransferStatsAmountServiceGrpc.TransferStatsAmountServiceImplBase {

    private final TransferAmountService transferAmountService;
    private final TransferAmountByCardService transferAmountByCardService;

    @Override
    public void findMonthlyTransferAmounts(FindYearTransferStatus request,
            StreamObserver<ApiResponseTransferMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse>> res = transferAmountService
                    .findMonthlyAmounts((long) request.getYear());

            ApiResponseTransferMonthAmount.Builder builder = ApiResponseTransferMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonth(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferAmounts(FindYearTransferStatus request,
            StreamObserver<ApiResponseTransferYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse>> res = transferAmountService
                    .findYearlyAmounts((long) request.getYear());

            ApiResponseTransferYearAmount.Builder builder = ApiResponseTransferYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYear(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransferAmountsBySenderCardNumber(FindByCardNumberTransferRequest request,
            StreamObserver<ApiResponseTransferMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse>> res = transferAmountByCardService
                    .findMonthlyAmountsBySender(toMonthYearCardNumber(request));

            ApiResponseTransferMonthAmount.Builder builder = ApiResponseTransferMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonth(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransferAmountsByReceiverCardNumber(FindByCardNumberTransferRequest request,
            StreamObserver<ApiResponseTransferMonthAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse>> res = transferAmountByCardService
                    .findMonthlyAmountsByReceiver(toMonthYearCardNumber(request));

            ApiResponseTransferMonthAmount.Builder builder = ApiResponseTransferMonthAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoMonth(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferAmountsBySenderCardNumber(FindByCardNumberTransferRequest request,
            StreamObserver<ApiResponseTransferYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse>> res = transferAmountByCardService
                    .findYearlyAmountsBySender(toMonthYearCardNumber(request));

            ApiResponseTransferYearAmount.Builder builder = ApiResponseTransferYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYear(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferAmountsByReceiverCardNumber(FindByCardNumberTransferRequest request,
            StreamObserver<ApiResponseTransferYearAmount> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse>> res = transferAmountByCardService
                    .findYearlyAmountsByReceiver(toMonthYearCardNumber(request));

            ApiResponseTransferYearAmount.Builder builder = ApiResponseTransferYearAmount.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            if (res.getData() != null) {
                res.getData().forEach(r -> builder.addData(toProtoYear(r)));
            }

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthYearCardNumber toMonthYearCardNumber(FindByCardNumberTransferRequest request) {
        MonthYearCardNumber req = new MonthYearCardNumber();
        req.setCardNumber(request.getCardNumber());
        req.setYear(request.getYear());
        return req;
    }

    private TransferMonthAmountResponse toProtoMonth(
            com.sanedge.paymentgateway.transfer.domain.responses.TransferMonthAmountResponse r) {
        return TransferMonthAmountResponse.newBuilder()
                .setMonth(ProtoMappers.nvl(r.getMonthName()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }

    private TransferYearAmountResponse toProtoYear(
            com.sanedge.paymentgateway.transfer.domain.responses.TransferYearAmountResponse r) {
        return TransferYearAmountResponse.newBuilder()
                .setYear(ProtoMappers.nvl(r.getReportYear()))
                .setTotalAmount(r.getTotalAmount() == null ? 0 : r.getTotalAmount().intValue())
                .build();
    }
}
