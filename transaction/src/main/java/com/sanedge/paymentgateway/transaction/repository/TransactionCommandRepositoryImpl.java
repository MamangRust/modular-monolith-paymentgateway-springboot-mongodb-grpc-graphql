package com.sanedge.paymentgateway.transaction.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.transaction.models.Transaction;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionCommandRepositoryImpl implements TransactionCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TransactionCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Transaction updateTransactionStatus(String transactionId, String status) {
        Query query = Query.query(Criteria.where("transactionId").is(transactionId)
                .and("deletedAt").isNull());
        Update update = new Update().set("status", status)
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transaction.class);
    }

    @Override
    public Transaction trashed(String transactionId) {
        Query query = Query.query(Criteria.where("transactionId").is(transactionId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transaction.class);
    }

    @Override
    public Transaction restore(String transactionId) {
        Query query = Query.query(Criteria.where("transactionId").is(transactionId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transaction.class);
    }

    @Override
    public boolean deletePermanent(String transactionId) {
        return mongoTemplate.remove(Query.query(Criteria.where("transactionId").is(transactionId)), Transaction.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Transaction.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Transaction.class)
                .getDeletedCount() > 0;
    }
}
