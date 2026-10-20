package com.sanedge.paymentgateway.saldo.handler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.grpc.GrpcErrorMapper;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.pb.saldo.FindYearlySaldo;
import com.sanedge.paymentgateway.pb.saldo.stats.ApiResponseMonthSaldoBalances;
import com.sanedge.paymentgateway.pb.saldo.stats.ApiResponseYearSaldoBalances;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoMonthBalanceResponse;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoStatsBalanceServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.stats.SaldoYearBalanceResponse;
import com.sanedge.paymentgateway.saldo.service.stats.SaldoBalanceService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaldoStatsBalanceHandler extends SaldoStatsBalanceServiceGrpc.SaldoStatsBalanceServiceImplBase {

    private final SaldoBalanceService saldoBalanceService;

    @Override
    public void findMonthlySaldoBalances(FindYearlySaldo request,
            StreamObserver<ApiResponseMonthSaldoBalances> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.saldo.domain.responses.stats.balance.SaldoMonthBalanceResponse>> res = saldoBalanceService
                    .getMonthBalance((long) request.getYear());

            ApiResponseMonthSaldoBalances.Builder builder = ApiResponseMonthSaldoBalances.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(SaldoMonthBalanceResponse.newBuilder()
                    .setMonth(ProtoMappers.nvl(r.getMonthName()))
                    .setTotalBalance(r.getTotalBalance() == null ? 0 : r.getTotalBalance().intValue())
                    .build()));

            observer.onNext(builder.build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(GrpcErrorMapper.toStatusRuntimeException(e));
        }
    }

    @Override
    public void findYearlySaldoBalances(FindYearlySaldo request,
            StreamObserver<ApiResponseYearSaldoBalances> observer) {
        try {
            ApiResponse<List<com.sanedge.paymentgateway.saldo.domain.responses.stats.balance.SaldoYearBalanceResponse>> res = saldoBalanceService
                    .getYearBalance((long) request.getYear());

            ApiResponseYearSaldoBalances.Builder builder = ApiResponseYearSaldoBalances.newBuilder()
                    .setStatus(ProtoMappers.nvl(res.getStatus()))
                    .setMessage(ProtoMappers.nvl(res.getMessage()));
            res.getData().forEach(r -> builder.addData(SaldoYearBalanceResponse.newBuilder()
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
