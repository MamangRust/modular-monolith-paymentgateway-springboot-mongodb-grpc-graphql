package com.sanedge.paymentgateway.merchant.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregation projection of a {@code transactions} document joined with its
 * {@code merchants} document (INNER JOIN), replacing the former native-SQL
 * projection interface.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTransactions {
    private String transactionId;
    private String cardNumber;
    private Long amount;
    private String paymentMethod;
    private String merchantId;
    private String merchantName;
    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
