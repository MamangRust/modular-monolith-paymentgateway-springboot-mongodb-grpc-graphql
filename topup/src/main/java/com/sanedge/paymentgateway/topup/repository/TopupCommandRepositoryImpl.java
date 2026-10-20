package com.sanedge.paymentgateway.topup.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.topup.models.Topup;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class TopupCommandRepositoryImpl implements TopupCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TopupCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Topup updateTopupAmount(String topupId, Long amount) {
        Query query = Query.query(Criteria.where("topupId").is(topupId)
                .and("deletedAt").isNull());
        Update update = new Update().set("topupAmount", amount)
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Topup.class);
    }

    @Override
    public Topup updateTopupStatus(String topupId, String status) {
        Query query = Query.query(Criteria.where("topupId").is(topupId)
                .and("deletedAt").isNull());
        Update update = new Update().set("status", status)
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Topup.class);
    }

    @Override
    public Topup trashed(String topupId) {
        Query query = Query.query(Criteria.where("topupId").is(topupId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Topup.class);
    }

    @Override
    public Topup restore(String topupId) {
        Query query = Query.query(Criteria.where("topupId").is(topupId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Topup.class);
    }

    @Override
    public boolean deletePermanent(String topupId) {
        return mongoTemplate.remove(Query.query(Criteria.where("topupId").is(topupId)), Topup.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Topup.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Topup.class)
                .getDeletedCount() > 0;
    }
}
