package com.sanedge.paymentgateway.topup.repository;

import com.sanedge.paymentgateway.topup.models.Topup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopupCommandRepository extends MongoRepository<Topup, String>, TopupCommandRepositoryCustom {

}
