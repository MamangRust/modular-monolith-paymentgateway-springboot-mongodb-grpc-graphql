package com.sanedge.paymentgateway.user.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.sanedge.paymentgateway.user.models.User;
import com.sanedge.paymentgateway.user.models.UserRole;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class UserCommandRepositoryImpl implements UserCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public UserCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User trashed(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), User.class);
    }

    @Override
    public User restore(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), User.class);
    }

    @Override
    public User deletePermanent(String userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        User user = mongoTemplate.findOne(query, User.class);
        if (user != null) {
            // Handle child relations first
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), UserRole.class);
            mongoTemplate.remove(query, User.class);
        }
        return user;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), User.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        List<String> trashedIds = mongoTemplate
                .find(Query.query(Criteria.where("deletedAt").ne(null)), User.class)
                .stream()
                .map(User::getUserId)
                .toList();

        // Handle child relations first
        if (!trashedIds.isEmpty()) {
            mongoTemplate.remove(Query.query(Criteria.where("userId").in(trashedIds)), UserRole.class);
        }

        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), User.class)
                .getDeletedCount() > 0;
    }
}
