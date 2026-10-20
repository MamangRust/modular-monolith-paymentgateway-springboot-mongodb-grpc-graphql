package com.sanedge.paymentgateway.saldo.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.saldo.models.Saldo;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class SaldoCommandRepositoryImpl implements SaldoCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public SaldoCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Saldo trashed(String saldoId) {
        Query query = Query.query(Criteria.where("saldoId").is(saldoId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Saldo.class);
    }

    @Override
    public Saldo restore(String saldoId) {
        Query query = Query.query(Criteria.where("saldoId").is(saldoId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Saldo.class);
    }

    @Override
    public int updateBalanceByCardNumber(String cardNumber, Long newBalance) {
        Query query = Query.query(Criteria.where("cardNumber").is(cardNumber)
                .and("deletedAt").isNull());
        Update update = new Update()
                .set("totalBalance", newBalance)
                .set("updatedAt", LocalDateTime.now());
        return (int) mongoTemplate.updateMulti(query, update, Saldo.class).getModifiedCount();
    }

    @Override
    public int updateWithdrawByCardNumber(String cardNumber, Long withdrawAmount) {
        Query query = Query.query(Criteria.where("cardNumber").is(cardNumber)
                .and("deletedAt").isNull());
        Update update = new Update()
                .set("withdrawAmount", withdrawAmount)
                .set("withdrawTime", LocalDateTime.now())
                .set("updatedAt", LocalDateTime.now());
        return (int) mongoTemplate.updateMulti(query, update, Saldo.class).getModifiedCount();
    }

    @Override
    public boolean deletePermanent(String saldoId) {
        return mongoTemplate.remove(Query.query(Criteria.where("saldoId").is(saldoId)), Saldo.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Saldo.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Saldo.class)
                .getDeletedCount() > 0;
    }
}
