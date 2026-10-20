package com.sanedge.paymentgateway.merchant.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.merchant.models.MerchantDocument;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/** Soft-delete statements for {@code merchant_documents}, mirroring the merchant repository impl. */
@Repository
public class MerchantDocumentCommandRepositoryImpl implements MerchantDocumentCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public MerchantDocumentCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MerchantDocument trashed(String documentId) {
        Query query = Query.query(Criteria.where("documentId").is(documentId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), MerchantDocument.class);
    }

    @Override
    public MerchantDocument restore(String documentId) {
        Query query = Query.query(Criteria.where("documentId").is(documentId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), MerchantDocument.class);
    }

    @Override
    public MerchantDocument deletePermanent(String documentId) {
        Query query = Query.query(Criteria.where("documentId").is(documentId)
                .and("deletedAt").ne(null));
        return mongoTemplate.findAndRemove(query, MerchantDocument.class);
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), MerchantDocument.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), MerchantDocument.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean updateStatus(String documentId, String status) {
        Query query = Query.query(Criteria.where("documentId").is(documentId)
                .and("deletedAt").isNull());
        Update update = new Update().set("status", status)
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), MerchantDocument.class) != null;
    }
}
