package com.sanedge.paymentgateway.withdraw.repository;

import com.sanedge.paymentgateway.withdraw.models.Withdraw;

public interface WithdrawCommandRepositoryCustom {
    Withdraw updateStatus(String withdrawId, String status);

    Withdraw trashed(String withdrawId);

    Withdraw restore(String withdrawId);

    boolean deletePermanent(String withdrawId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
