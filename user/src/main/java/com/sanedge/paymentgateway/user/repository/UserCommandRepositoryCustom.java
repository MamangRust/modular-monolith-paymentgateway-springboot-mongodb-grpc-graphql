package com.sanedge.paymentgateway.user.repository;

import com.sanedge.paymentgateway.user.models.User;

public interface UserCommandRepositoryCustom {
    User trashed(String userId);

    User restore(String userId);

    User deletePermanent(String userId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
