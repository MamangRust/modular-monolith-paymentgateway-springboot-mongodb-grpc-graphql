package com.sanedge.paymentgateway.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.auth.models.RefreshToken;

@Repository
public interface RefreshTokenCommandRepository extends MongoRepository<RefreshToken, String> {

        int deleteByToken(String token);

        int deleteByUserId(String userId);
}
