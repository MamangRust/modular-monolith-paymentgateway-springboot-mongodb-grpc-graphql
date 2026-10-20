package com.sanedge.paymentgateway.transfer.repository;

import com.sanedge.paymentgateway.transfer.models.Transfer;

public interface TransferCommandRepositoryCustom {
    Transfer updateTransferAmount(String transferId, Long amount);

    Transfer updateTransferStatus(String transferId, String status);

    Transfer trashed(String transferId);

    Transfer restore(String transferId);

    boolean deletePermanent(String transferId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
