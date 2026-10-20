package com.sanedge.paymentgateway.user.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.user.models.UserRole;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {

    List<UserRole> findByUserId(String userId);

    void deleteByUserId(String userId);
}
