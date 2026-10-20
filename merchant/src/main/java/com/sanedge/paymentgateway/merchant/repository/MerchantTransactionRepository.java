package com.sanedge.paymentgateway.merchant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.paymentgateway.merchant.models.MerchantTransactions;
import com.sanedge.paymentgateway.merchant.models.Transaction;

/**
 * Aggregation of {@code transactions} joined (INNER JOIN) with {@code merchants}
 * on {@code merchant_id}, replacing the former native-SQL queries. Aggregation
 * methods cannot return {@code Page}, so the paged queries are backed by
 * {@link MerchantTransactionRepositoryCustom}.
 */
@Repository
public interface MerchantTransactionRepository
        extends MongoRepository<Transaction, String>, MerchantTransactionRepositoryCustom {

    Page<MerchantTransactions> findAllTransactions(String search, Pageable pageable);

    Page<MerchantTransactions> findAllTransactionsByMerchant(String merchantId, String search, Pageable pageable);

    Page<MerchantTransactions> findAllTransactionsByApiKey(String apiKey, String search, Pageable pageable);
}
