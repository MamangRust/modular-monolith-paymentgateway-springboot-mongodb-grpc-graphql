package com.sanedge.paymentgateway.withdraw.service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.withdraw.domain.requests.CreateWithdrawRequest;
import com.sanedge.paymentgateway.withdraw.domain.requests.UpdateWithdrawRequest;
import com.sanedge.paymentgateway.withdraw.domain.responses.WithdrawResponse;
import com.sanedge.paymentgateway.withdraw.domain.responses.WithdrawResponseDeleteAt;

public interface WithdrawCommandService {

    ApiResponse<WithdrawResponse> create(CreateWithdrawRequest req);

    ApiResponse<WithdrawResponse> update(UpdateWithdrawRequest req);

    ApiResponse<WithdrawResponseDeleteAt> trashed(String withdrawId);

    ApiResponse<WithdrawResponseDeleteAt> restore(String withdrawId);

    ApiResponse<Boolean> deletePermanent(String withdrawId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
