package com.sanedge.paymentgateway.withdraw.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.withdraw.models.Withdraw;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class WithdrawCommandRepositoryImpl implements WithdrawCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public WithdrawCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Withdraw updateStatus(String withdrawId, String status) {
        Query query = Query.query(Criteria.where("withdrawId").is(withdrawId)
                .and("deletedAt").isNull());
        Update update = new Update().set("status", status);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Withdraw.class);
    }

    @Override
    public Withdraw trashed(String withdrawId) {
        Query query = Query.query(Criteria.where("withdrawId").is(withdrawId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Withdraw.class);
    }

    @Override
    public Withdraw restore(String withdrawId) {
        Query query = Query.query(Criteria.where("withdrawId").is(withdrawId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Withdraw.class);
    }

    @Override
    public boolean deletePermanent(String withdrawId) {
        return mongoTemplate.remove(Query.query(Criteria.where("withdrawId").is(withdrawId)), Withdraw.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Withdraw.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Withdraw.class)
                .getDeletedCount() > 0;
    }
}
