package com.sanedge.paymentgateway.role.repository;

import java.util.Optional;
import java.util.regex.Pattern;

import com.sanedge.paymentgateway.role.models.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleQueryRepository extends MongoRepository<Role, String>, RoleQueryRepositoryCustom {

    Page<Role> findByDeletedAtIsNull(Pageable pageable);

    Page<Role> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<Role> findByRoleName(String roleName);

    @Query("{ 'role_name': { $regex: ?0, $options: 'i' } }")
    Page<Role> searchRoles(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': null, 'role_name': { $regex: ?0, $options: 'i' } }")
    Page<Role> searchActiveRoles(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, 'role_name': { $regex: ?0, $options: 'i' } }")
    Page<Role> searchTrashedRoles(String keyword, Pageable pageable);

    default Page<Role> findRoles(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt"));
        return (keyword == null || keyword.isBlank())
                ? findAll(sorted)
                : searchRoles(Pattern.quote(keyword), sorted);
    }

    default Page<Role> findActiveRoles(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt"));
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(sorted)
                : searchActiveRoles(Pattern.quote(keyword), sorted);
    }

    default Page<Role> findTrashedRoles(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "deletedAt"));
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(sorted)
                : searchTrashedRoles(Pattern.quote(keyword), sorted);
    }
}
