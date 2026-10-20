package com.sanedge.paymentgateway.topup.repository;

import com.sanedge.paymentgateway.topup.models.Topup;

public interface TopupCommandRepositoryCustom {
    Topup updateTopupAmount(String topupId, Long amount);

    Topup updateTopupStatus(String topupId, String status);

    Topup trashed(String topupId);

    Topup restore(String topupId);

    boolean deletePermanent(String topupId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
