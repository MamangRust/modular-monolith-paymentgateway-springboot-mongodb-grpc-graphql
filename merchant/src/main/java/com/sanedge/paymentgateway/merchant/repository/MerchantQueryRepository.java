package com.sanedge.paymentgateway.merchant.repository;

import com.sanedge.paymentgateway.merchant.models.Merchant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public interface MerchantQueryRepository extends MongoRepository<Merchant, String> {

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<Merchant> findByMerchantIdAndDeletedAtIsNull(String merchantId);

    Optional<Merchant> findByApiKeyAndDeletedAtIsNull(String apiKey);

    Optional<Merchant> findFirstByNameIgnoreCaseAndDeletedAtIsNull(String name);

    List<Merchant> findByUserIdAndDeletedAtIsNull(String userId);

    Page<Merchant> findByDeletedAtIsNull(Pageable pageable);

    Page<Merchant> findByDeletedAtIsNotNull(Pageable pageable);

    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'api_key': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Merchant> searchMerchants(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': null, $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'api_key': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Merchant> searchActiveMerchants(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'api_key': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Merchant> searchTrashedMerchants(String keyword, Pageable pageable);

    default boolean existsByName(String name) {
        return existsByNameIgnoreCaseAndDeletedAtIsNull(name);
    }

    default Optional<Merchant> findMerchantById(String merchantId) {
        return findByMerchantIdAndDeletedAtIsNull(merchantId);
    }

    default Optional<Merchant> findByApiKey(String apiKey) {
        return findByApiKeyAndDeletedAtIsNull(apiKey);
    }

    default Optional<Merchant> findByName(String name) {
        return findFirstByNameIgnoreCaseAndDeletedAtIsNull(name);
    }

    default List<Merchant> findByUserId(String userId) {
        return findByUserIdAndDeletedAtIsNull(userId);
    }

    default Page<Merchant> findMerchants(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "merchantId"));
        return (keyword == null || keyword.isBlank())
                ? findAll(sorted)
                : searchMerchants(Pattern.quote(keyword), sorted);
    }

    default Page<Merchant> findActiveMerchants(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "merchantId"));
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(sorted)
                : searchActiveMerchants(Pattern.quote(keyword), sorted);
    }

    default Page<Merchant> findTrashedMerchants(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "merchantId"));
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(sorted)
                : searchTrashedMerchants(Pattern.quote(keyword), sorted);
    }
}
