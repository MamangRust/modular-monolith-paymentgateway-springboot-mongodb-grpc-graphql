package com.sanedge.paymentgateway.transfer.repository;

import com.sanedge.paymentgateway.transfer.models.Transfer;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferQueryRepository extends MongoRepository<Transfer, String> {

    Page<Transfer> findByDeletedAtIsNull(Pageable pageable);

    Page<Transfer> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<Transfer> findByTransferIdAndDeletedAtIsNull(String transferId);

    @Query("{ 'deleted_at': null, $or: [ { 'transfer_from': { $regex: ?0, $options: 'i' } }, { 'transfer_to': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Transfer> searchActiveTransfers(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'transfer_from': { $regex: ?0, $options: 'i' } }, { 'transfer_to': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Transfer> searchTrashedTransfers(String keyword, Pageable pageable);

    default Page<Transfer> findTransfers(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveTransfers(Pattern.quote(keyword), pageable);
    }

    default Page<Transfer> findActiveTransfers(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveTransfers(Pattern.quote(keyword), pageable);
    }

    default Page<Transfer> findTrashedTransfers(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedTransfers(Pattern.quote(keyword), pageable);
    }

    default Optional<Transfer> findTransferById(String transferId) {
        return findByTransferIdAndDeletedAtIsNull(transferId);
    }

    List<Transfer> findByDeletedAtIsNullAndTransferFrom(String transferFrom, Sort sort);

    List<Transfer> findByDeletedAtIsNullAndTransferTo(String transferTo, Sort sort);

    @Query("{ 'deleted_at': null, $or: [ { 'transfer_from': ?0 }, { 'transfer_to': ?0 } ] }")
    List<Transfer> findActiveTransfersByCardNumber(String cardNumber, Sort sort);

    default List<Transfer> findTransfersByCardNumber(String cardNumber) {
        return findActiveTransfersByCardNumber(cardNumber, Sort.by(Sort.Direction.DESC, "transferTime"));
    }

    default List<Transfer> findTransfersBySourceCard(String cardNumber) {
        return findByDeletedAtIsNullAndTransferFrom(cardNumber, Sort.by(Sort.Direction.DESC, "transferTime"));
    }

    default List<Transfer> findTransfersByDestinationCard(String cardNumber) {
        return findByDeletedAtIsNullAndTransferTo(cardNumber, Sort.by(Sort.Direction.DESC, "transferTime"));
    }
}
