package com.sanedge.paymentgateway.merchant.service;

import com.sanedge.paymentgateway.merchant.domain.requests.CreateMerchantRequest;
import com.sanedge.paymentgateway.merchant.domain.requests.UpdateMerchantRequest;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.MerchantResponse;
import com.sanedge.paymentgateway.merchant.domain.responses.MerchantResponseDeleteAt;

public interface MerchantCommandService {
    ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchantStatus(String merchantId, String status);

    ApiResponse<MerchantResponseDeleteAt> trashMerchant(String id);

    ApiResponse<MerchantResponseDeleteAt> restoreMerchant(String id);

    ApiResponse<Boolean> deleteMerchant(String id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
