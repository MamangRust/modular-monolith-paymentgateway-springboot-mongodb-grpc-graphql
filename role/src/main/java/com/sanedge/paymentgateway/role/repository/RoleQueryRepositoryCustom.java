package com.sanedge.paymentgateway.role.repository;

import java.util.List;

import com.sanedge.paymentgateway.role.models.Role;

public interface RoleQueryRepositoryCustom {
    List<Role> findUserRoles(String userId);
}
