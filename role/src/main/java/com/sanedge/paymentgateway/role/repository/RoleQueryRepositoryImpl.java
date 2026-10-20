package com.sanedge.paymentgateway.role.repository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.sanedge.paymentgateway.role.models.Role;
import com.sanedge.paymentgateway.role.models.UserRole;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RoleQueryRepositoryImpl implements RoleQueryRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public RoleQueryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Role> findUserRoles(String userId) {
        List<UserRole> userRoles = mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId)), UserRole.class);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .toList();

        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        return mongoTemplate.find(
                Query.query(Criteria.where("roleId").in(roleIds))
                        .with(Sort.by(Sort.Direction.ASC, "createdAt")),
                Role.class);
    }
}
