package com.sanedge.paymentgateway.merchant.repository;

import com.sanedge.paymentgateway.merchant.models.Merchant;

public interface MerchantCommandRepositoryCustom {
    Merchant trashed(String merchantId);

    Merchant restore(String merchantId);

    boolean deletePermanent(String merchantId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    boolean updateStatus(String merchantId, String status);
}
