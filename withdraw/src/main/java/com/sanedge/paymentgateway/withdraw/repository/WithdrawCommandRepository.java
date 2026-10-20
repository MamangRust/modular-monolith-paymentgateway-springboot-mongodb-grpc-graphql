package com.sanedge.paymentgateway.withdraw.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.withdraw.models.Withdraw;

@Repository
public interface WithdrawCommandRepository extends MongoRepository<Withdraw, String>, WithdrawCommandRepositoryCustom {
}
