package com.sanedge.paymentgateway.merchant.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantDocument;

/** Command side for merchant documents; soft-delete ops live in the custom impl. */
@Repository
public interface MerchantDocumentCommandRepository
        extends MongoRepository<MerchantDocument, String>, MerchantDocumentCommandRepositoryCustom {

}
