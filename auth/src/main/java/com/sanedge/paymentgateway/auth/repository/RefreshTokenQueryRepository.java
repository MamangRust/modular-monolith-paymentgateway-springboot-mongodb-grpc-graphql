package com.sanedge.paymentgateway.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.auth.models.RefreshToken;

@Repository
public interface RefreshTokenQueryRepository extends MongoRepository<RefreshToken, String> {

        Optional<RefreshToken> findFirstByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(String userId);

        Optional<RefreshToken> findByTokenAndDeletedAtIsNull(String token);

        default Optional<RefreshToken> findByUserId(String userId) {
                return findFirstByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
        }

        default Optional<RefreshToken> findByToken(String token) {
                return findByTokenAndDeletedAtIsNull(token);
        }
}
