package com.sanedge.paymentgateway.merchant.repository;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantDocument;

/**
 * Read side for merchant documents. Port of the blueprint's
 * {@code MerchantDocumentQueryRepository} onto Spring Data MongoDB paging.
 */
@Repository
public interface MerchantDocumentQueryRepository extends MongoRepository<MerchantDocument, String> {

    Optional<MerchantDocument> findByDocumentIdAndDeletedAtIsNull(String documentId);

    Page<MerchantDocument> findByDeletedAtIsNull(Pageable pageable);

    Page<MerchantDocument> findByDeletedAtIsNotNull(Pageable pageable);

    @Query("{ $or: [ { 'document_type': { $regex: ?0, $options: 'i' } }, { 'document_url': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } }, { 'note': { $regex: ?0, $options: 'i' } } ] }")
    Page<MerchantDocument> searchDocuments(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': null, $or: [ { 'document_type': { $regex: ?0, $options: 'i' } }, { 'document_url': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } }, { 'note': { $regex: ?0, $options: 'i' } } ] }")
    Page<MerchantDocument> searchActiveDocuments(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'document_type': { $regex: ?0, $options: 'i' } }, { 'document_url': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } }, { 'note': { $regex: ?0, $options: 'i' } } ] }")
    Page<MerchantDocument> searchTrashedDocuments(String keyword, Pageable pageable);

    default Optional<MerchantDocument> findDocumentById(String documentId) {
        return findByDocumentIdAndDeletedAtIsNull(documentId);
    }

    default Page<MerchantDocument> findDocuments(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "documentId"));
        return (keyword == null || keyword.isBlank())
                ? findAll(sorted)
                : searchDocuments(Pattern.quote(keyword), sorted);
    }

    default Page<MerchantDocument> findActiveDocuments(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "documentId"));
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(sorted)
                : searchActiveDocuments(Pattern.quote(keyword), sorted);
    }

    default Page<MerchantDocument> findTrashedDocuments(String keyword, Pageable pageable) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "documentId"));
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(sorted)
                : searchTrashedDocuments(Pattern.quote(keyword), sorted);
    }
}
