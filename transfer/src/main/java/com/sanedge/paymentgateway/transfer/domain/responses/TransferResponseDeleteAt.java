package com.sanedge.paymentgateway.transfer.domain.responses;

import com.sanedge.paymentgateway.transfer.models.Transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDeleteAt {
    private String id;
    private String transferNo;
    private String transferFrom;
    private String transferTo;
    private Long transferAmount;
    private String transferTime;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static TransferResponseDeleteAt from(Transfer t) {
        return TransferResponseDeleteAt.builder()
                .id(t.getTransferId())
                .transferNo(t.getTransferNo())
                .transferFrom(t.getTransferFrom())
                .transferTo(t.getTransferTo())
                .transferAmount(t.getTransferAmount())
                .transferTime(t.getTransferTime() != null ? t.getTransferTime().toString() : null)
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                .updatedAt(t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null)
                .deletedAt(t.getDeletedAt() != null ? t.getDeletedAt().toString() : null)
                .build();
    }
}
