package com.sanedge.paymentgateway.card.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.card.ApiResponseMonthlyAmount;
import com.sanedge.paymentgateway.pb.card.ApiResponseYearlyAmount;
import com.sanedge.paymentgateway.pb.card.CardResponseMonthlyAmount;
import com.sanedge.paymentgateway.pb.card.CardResponseYearlyAmount;
import com.sanedge.paymentgateway.pb.card.FindYearAmount;
import com.sanedge.paymentgateway.pb.card.FindYearAmountCardNumber;
import com.sanedge.paymentgateway.pb.card.stats.CardStatsTransferServiceGrpc;
import com.sanedge.paymentgateway.card.domain.requests.MonthYearCardNumberCard;
import com.sanedge.paymentgateway.card.service.stats.transfer.CardTransferAmountByCardService;
import com.sanedge.paymentgateway.card.service.stats.transfer.CardTransferAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardStatsTransferHandler extends CardStatsTransferServiceGrpc.CardStatsTransferServiceImplBase {

    private final CardTransferAmountService cardTransferAmountService;
    private final CardTransferAmountByCardService cardTransferAmountByCardService;

    @Override
    public void findMonthlyTransferSenderAmount(FindYearAmount request,
            StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            observer.onNext(toMonthly(cardTransferAmountService.findMonthAmountSender((long) request.getYear())));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferSenderAmount(FindYearAmount request,
            StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            observer.onNext(toYearly(cardTransferAmountService.findYearAmountSender((long) request.getYear())));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransferReceiverAmount(FindYearAmount request,
            StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            observer.onNext(toMonthly(cardTransferAmountService.findMonthAmountReceiver((long) request.getYear())));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferReceiverAmount(FindYearAmount request,
            StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            observer.onNext(toYearly(cardTransferAmountService.findYearAmountReceiver((long) request.getYear())));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransferSenderAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            observer.onNext(toMonthly(cardTransferAmountByCardService.findMonthAmountSender(toRequest(request))));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferSenderAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            observer.onNext(toYearly(cardTransferAmountByCardService.findYearAmountSender(toRequest(request))));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findMonthlyTransferReceiverAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseMonthlyAmount> observer) {
        try {
            observer.onNext(toMonthly(cardTransferAmountByCardService.findMonthAmountReceiver(toRequest(request))));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlyTransferReceiverAmountByCardNumber(FindYearAmountCardNumber request,
            StreamObserver<ApiResponseYearlyAmount> observer) {
        try {
            observer.onNext(toYearly(cardTransferAmountByCardService.findYearAmountReceiver(toRequest(request))));
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    private MonthYearCardNumberCard toRequest(FindYearAmountCardNumber request) {
        return new MonthYearCardNumberCard(request.getCardNumber(), (long) request.getYear());
    }

    private ApiResponseMonthlyAmount toMonthly(
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseMonthAmount>> res) {
        ApiResponseMonthlyAmount.Builder builder = ApiResponseMonthlyAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            res.getData().forEach(r -> builder.addData(CardResponseMonthlyAmount.newBuilder()
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                    .build()));
        }
        return builder.build();
    }

    private ApiResponseYearlyAmount toYearly(
            ApiResponse<List<com.sanedge.paymentgateway.card.domain.responses.stats.amount.CardResponseYearAmount>> res) {
        ApiResponseYearlyAmount.Builder builder = ApiResponseYearlyAmount.newBuilder()
                .setStatus(ProtoMappers.nvl(res.getStatus()))
                .setMessage(ProtoMappers.nvl(res.getMessage()));
        if (res.getData() != null) {
            res.getData().forEach(r -> builder.addData(CardResponseYearlyAmount.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalAmount(r.getTotalAmount() == null ? 0L : r.getTotalAmount())
                    .build()));
        }
        return builder.build();
    }
}
