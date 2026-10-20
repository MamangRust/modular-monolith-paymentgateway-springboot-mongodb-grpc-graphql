package com.sanedge.paymentgateway.transaction.repository;

import com.sanedge.paymentgateway.transaction.models.Transaction;

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
public interface TransactionQueryRepository extends MongoRepository<Transaction, String> {

    Page<Transaction> findByDeletedAtIsNull(Pageable pageable);

    Page<Transaction> findByDeletedAtIsNotNull(Pageable pageable);

    Optional<Transaction> findByTransactionIdAndDeletedAtIsNull(String transactionId);

    Page<Transaction> findByCardNumberAndDeletedAtIsNull(String cardNumber, Pageable pageable);

    List<Transaction> findByMerchantIdAndDeletedAtIsNull(String merchantId, Sort sort);

    @Query("{ 'deleted_at': null, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'payment_method': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Transaction> searchActiveTransactions(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': { $ne: null }, $or: [ { 'card_number': { $regex: ?0, $options: 'i' } }, { 'payment_method': { $regex: ?0, $options: 'i' } }, { 'status': { $regex: ?0, $options: 'i' } } ] }")
    Page<Transaction> searchTrashedTransactions(String keyword, Pageable pageable);

    @Query("{ 'deleted_at': null, 'card_number': ?0, $or: [ { 'payment_method': { $regex: ?1, $options: 'i' } }, { 'status': { $regex: ?1, $options: 'i' } } ] }")
    Page<Transaction> searchActiveTransactionsByCardNumber(String cardNumber, String keyword, Pageable pageable);

    default Page<Transaction> findTransactions(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveTransactions(Pattern.quote(keyword), pageable);
    }

    default Page<Transaction> findActiveTransactions(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNull(pageable)
                : searchActiveTransactions(Pattern.quote(keyword), pageable);
    }

    default Page<Transaction> findTrashedTransactions(String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByDeletedAtIsNotNull(pageable)
                : searchTrashedTransactions(Pattern.quote(keyword), pageable);
    }

    default Transaction findTransactionById(String transactionId) {
        return findByTransactionIdAndDeletedAtIsNull(transactionId).orElse(null);
    }

    default Page<Transaction> findTransactionsByCardNumber(String cardNumber, String keyword, Pageable pageable) {
        return (keyword == null || keyword.isBlank())
                ? findByCardNumberAndDeletedAtIsNull(cardNumber, pageable)
                : searchActiveTransactionsByCardNumber(cardNumber, Pattern.quote(keyword), pageable);
    }

    default List<Transaction> findTransactionsByMerchantId(String merchantId) {
        return findByMerchantIdAndDeletedAtIsNull(merchantId, Sort.by(Sort.Direction.DESC, "transactionTime"));
    }
}
