package com.sanedge.paymentgateway.role.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.role.models.UserRole;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {

}
