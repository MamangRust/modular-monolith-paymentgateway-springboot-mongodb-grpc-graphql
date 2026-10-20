package com.sanedge.paymentgateway.merchant.service;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.requests.CreateMerchantDocumentRequest;
import com.sanedge.paymentgateway.merchant.domain.requests.UpdateMerchantDocumentRequest;
import com.sanedge.paymentgateway.merchant.domain.requests.UpdateMerchantDocumentStatus;
import com.sanedge.paymentgateway.merchant.domain.responses.MerchantDocumentResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.MerchantDocumentResponseDeleteAt;

/** Command side of merchant documents. */
public interface MerchantDocumentCommandService {

    ApiResponse<MerchantDocumentResponse> create(CreateMerchantDocumentRequest req);

    ApiResponse<MerchantDocumentResponse> update(UpdateMerchantDocumentRequest req);

    ApiResponse<MerchantDocumentResponse> updateStatus(UpdateMerchantDocumentStatus req);

    ApiResponse<MerchantDocumentResponseDeleteAt> trash(String documentId);

    ApiResponse<MerchantDocumentResponseDeleteAt> restore(String documentId);

    ApiResponse<Void> deletePermanent(String documentId);

    ApiResponse<Void> restoreAll();

    ApiResponse<Void> deleteAllPermanent();
}
