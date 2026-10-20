package com.sanedge.paymentgateway.card.repository;

import com.sanedge.paymentgateway.card.models.Card;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardCommandRepository extends MongoRepository<Card, String>, CardCommandRepositoryCustom {

}
