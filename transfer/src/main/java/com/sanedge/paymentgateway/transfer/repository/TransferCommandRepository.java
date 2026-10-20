package com.sanedge.paymentgateway.transfer.repository;

import com.sanedge.paymentgateway.transfer.models.Transfer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferCommandRepository extends MongoRepository<Transfer, String>, TransferCommandRepositoryCustom {

}
