package com.sanedge.paymentgateway.role.repository;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.role.models.Role;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class RoleCommandRepositoryImpl implements RoleCommandRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public RoleCommandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Role trashed(String roleId) {
        Query query = Query.query(Criteria.where("roleId").is(roleId)
                .and("deletedAt").isNull());
        Update update = new Update().set("deletedAt", LocalDateTime.now());
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Role.class);
    }

    @Override
    public Role restore(String roleId) {
        Query query = Query.query(Criteria.where("roleId").is(roleId)
                .and("deletedAt").ne(null));
        Update update = new Update().set("deletedAt", null);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), Role.class);
    }

    @Override
    public Role deletePermanent(String roleId) {
        Query query = Query.query(Criteria.where("roleId").is(roleId));
        Role role = mongoTemplate.findOne(query, Role.class);
        if (role != null) {
            mongoTemplate.remove(query, Role.class);
        }
        return role;
    }

    @Override
    public boolean restoreAllDeleted() {
        return mongoTemplate.updateMulti(
                Query.query(Criteria.where("deletedAt").ne(null)),
                new Update().set("deletedAt", null), Role.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteAllDeleted() {
        return mongoTemplate.remove(Query.query(Criteria.where("deletedAt").ne(null)), Role.class)
                .getDeletedCount() > 0;
    }
}
