package com.sanedge.paymentgateway.user.repository;

import com.sanedge.paymentgateway.user.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommandRepository extends MongoRepository<User, String>, UserCommandRepositoryCustom {

}
