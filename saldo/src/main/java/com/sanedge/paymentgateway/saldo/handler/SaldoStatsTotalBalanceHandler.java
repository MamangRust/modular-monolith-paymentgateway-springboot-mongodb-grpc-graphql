package com.sanedge.paymentgateway.saldo.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.saldo.FindMonthlySaldoTotalBalance;
import com.sanedge.paymentgateway.pb.saldo.FindYearlySaldo;
import com.sanedge.paymentgateway.pb.saldo.stats.ApiResponseMonthTotalSaldo;
import com.sanedge.paymentgateway.pb.saldo.stats.ApiResponseYearTotalSaldo;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoMonthTotalBalanceResponse;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoStatsTotalBalanceGrpc;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoYearTotalBalanceResponse;
import com.sanedge.paymentgateway.saldo.domain.requests.MonthTotalSaldoBalance;
import com.sanedge.paymentgateway.saldo.service.stats.SaldoTotalAmountService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaldoStatsTotalBalanceHandler extends SaldoStatsTotalBalanceGrpc.SaldoStatsTotalBalanceImplBase {

    private final SaldoTotalAmountService saldoTotalAmountService;

    @Override
    public void findMonthlyTotalSaldoBalance(FindMonthlySaldoTotalBalance request,
            StreamObserver<ApiResponseMonthTotalSaldo> observer) {
        try {
            MonthTotalSaldoBalance req = new MonthTotalSaldoBalance();
            req.setYear((long) request.getYear());
            req.setMonth(request.getMonth());

            ApiResponse<List<com.sanedge.paymentgateway.saldo.domain.responses.stats.total_balance.SaldoMonthTotalBalanceResponse>> res = saldoTotalAmountService
                    .findMonthTotalBalance(req);

            ApiResponseMonthTotalSaldo.Builder builder = ApiResponseMonthTotalSaldo.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(SaldoMonthTotalBalanceResponse.newBuilder()
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalBalance(r.getTotalBalance() == null ? 0 : r.getTotalBalance().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearTotalSaldoBalance(FindYearlySaldo request, StreamObserver<ApiResponseYearTotalSaldo> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.saldo.domain.responses.stats.total_balance.SaldoYearTotalBalanceResponse>> res = saldoTotalAmountService
                    .findYearTotalBalance((long) request.getYear());

            ApiResponseYearTotalSaldo.Builder builder = ApiResponseYearTotalSaldo.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(SaldoYearTotalBalanceResponse.newBuilder()
                    .setYear(ProtoMappers.nvl(r.getReportYear()))
                    .setTotalBalance(r.getTotalBalance() == null ? 0 : r.getTotalBalance().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }
}
