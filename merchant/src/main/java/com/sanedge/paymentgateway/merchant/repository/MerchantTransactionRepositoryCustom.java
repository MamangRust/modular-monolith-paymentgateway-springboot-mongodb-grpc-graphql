package com.sanedge.paymentgateway.merchant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sanedge.paymentgateway.merchant.models.MerchantTransactions;

public interface MerchantTransactionRepositoryCustom {

    Page<MerchantTransactions> findAllTransactions(String search, Pageable pageable);

    Page<MerchantTransactions> findAllTransactionsByMerchant(String merchantId, String search, Pageable pageable);

    Page<MerchantTransactions> findAllTransactionsByApiKey(String apiKey, String search, Pageable pageable);
}
