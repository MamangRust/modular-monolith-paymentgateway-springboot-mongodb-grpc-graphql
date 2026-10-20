package com.sanedge.paymentgateway.card.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.card.models.Card;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class CardCommandRepositoryImpl implements CardCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public CardCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Card trashed(String cardId) {
        Query query = Query.query(Criteria.where("cardId").is(cardId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Card.class);
    }

    @Override
    public Card restore(String cardId) {
        Query query = Query.query(Criteria.where("cardId").is(cardId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Card.class);
    }

    @Override
    public boolean deletePermanent(String cardId) {
        return mongoTemplate.remove(Query.query(Criteria.where("cardId").is(cardId)), Card.class)
                .getDeletedCount() > 0;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Card.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Card.class)
                .getDeletedCount() > 0;
    }
}
