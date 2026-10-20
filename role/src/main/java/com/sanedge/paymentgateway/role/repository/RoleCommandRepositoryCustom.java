package com.sanedge.paymentgateway.role.repository;

import com.sanedge.paymentgateway.role.models.Role;

public interface RoleCommandRepositoryCustom {
    Role trashed(String roleId);

    Role restore(String roleId);

    Role deletePermanent(String roleId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
