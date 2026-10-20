package com.sanedge.paymentgateway.merchant.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.merchant.models.Merchant;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class MerchantCommandRepositoryImpl implements MerchantCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public MerchantCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Merchant trashed(String merchantId) {
        Query query = Query.query(Criteria.where("merchantId").is(merchantId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Merchant.class);
    }

    @Override
    public Merchant restore(String merchantId) {
        Query query = Query.query(Criteria.where("merchantId").is(merchantId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Merchant.class);
    }

    @Override
    public boolean deletePermanent(String merchantId) {
        return mongoTemplate.remove(Query.query(Criteria.where("merchantId").is(merchantId)), Merchant.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Merchant.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Merchant.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean updateStatus(String merchantId, String status) {
        Query query = Query.query(Criteria.where("merchantId").is(merchantId)
                .and("deletedAt").isNull());
        Update update = new Update().set("status", status)
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Merchant.class) != null;
    }
}
