package com.sanedge.paymentgateway.saldo.repository;

import com.sanedge.paymentgateway.saldo.models.Saldo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaldoCommandRepository extends MongoRepository<Saldo, String>, SaldoCommandRepositoryCustom {

}
