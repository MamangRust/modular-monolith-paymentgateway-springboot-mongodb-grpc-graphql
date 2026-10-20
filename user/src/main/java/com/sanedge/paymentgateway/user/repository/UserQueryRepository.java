package com.sanedge.paymentgateway.user.repository;

import java.util.Optional;
import java.util.regex.Pattern;

import com.sanedge.paymentgateway.user.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueryRepository extends MongoRepository<User, String> {

    Page<User> findByDeletedAtIsNull(Pageable pageable);

    Page<User> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<User> findByUserIdAndDeletedAtIsNull(String userId);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    @Query("{ 'deleted_at': null, $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'firstname': { $regex: ?0, $options: 'i' } }, { 'lastname': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchActiveUsers(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'username': { $regex: ?0, $options: 'i' } }, { 'firstname': { $regex: ?0, $options: 'i' } }, { 'lastname': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchTrashedUsers(String keyword, Pageable pageable);

    default Page<User> findAllUsers(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveUsers(Pattern.quote(keyword), pageable);
    }

    default Page<User> findActiveUsers(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveUsers(Pattern.quote(keyword), pageable);
    }

    default Page<User> findTrashedUsers(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedUsers(Pattern.quote(keyword), pageable);
    }

    default Optional<User> findByUserId(String userId) {
        return findByUserIdAndDeletedAtIsNull(userId);
    }

    default Optional<User> findByEmail(String email) {
        return findByEmailAndDeletedAtIsNull(email);
    }

    default Optional<User> findByUsername(String username) {
        return findByUsernameAndDeletedAtIsNull(username);
    }
}
