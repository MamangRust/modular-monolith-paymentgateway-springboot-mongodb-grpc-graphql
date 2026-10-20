package com.sanedge.paymentgateway.merchant.repository;

import com.sanedge.paymentgateway.merchant.models.MerchantDocument;

/** Soft-delete operations for {@code merchant_documents}. */
public interface MerchantDocumentCommandRepositoryCustom {

    MerchantDocument trashed(String documentId);

    MerchantDocument restore(String documentId);

    MerchantDocument deletePermanent(String documentId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    boolean updateStatus(String documentId, String status);
}
