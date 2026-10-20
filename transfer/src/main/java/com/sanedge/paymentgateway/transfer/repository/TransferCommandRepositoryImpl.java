package com.sanedge.paymentgateway.transfer.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.transfer.models.Transfer;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class TransferCommandRepositoryImpl implements TransferCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TransferCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Transfer updateTransferAmount(String transferId, Long amount) {
        Query query = Query.query(Criteria.where("transferId").is(transferId)
                .and("deletedAt").isNull());
        Update update = new Update()
                .set("transferAmount", amount)
                .set("transferTime", LocalDateTime.now())
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transfer.class);
    }

    @Override
    public Transfer updateTransferStatus(String transferId, String status) {
        Query query = Query.query(Criteria.where("transferId").is(transferId)
                .and("deletedAt").isNull());
        Update update = new Update()
                .set("status", status)
                .set("updatedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transfer.class);
    }

    @Override
    public Transfer trashed(String transferId) {
        Query query = Query.query(Criteria.where("transferId").is(transferId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transfer.class);
    }

    @Override
    public Transfer restore(String transferId) {
        Query query = Query.query(Criteria.where("transferId").is(transferId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Transfer.class);
    }

    @Override
    public boolean deletePermanent(String transferId) {
        return mongoTemplate.remove(Query.query(Criteria.where("transferId").is(transferId)), Transfer.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Transfer.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Transfer.class)
                .getDeletedCount() > 0;
    }
}
