package com.sanedge.paymentgateway.transaction.repository;

import com.sanedge.paymentgateway.transaction.models.Transaction;

public interface TransactionCommandRepositoryCustom {
    Transaction updateTransactionStatus(String transactionId, String status);

    Transaction trashed(String transactionId);

    Transaction restore(String transactionId);

    boolean deletePermanent(String transactionId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
