package com.sanedge.paymentgateway.merchant.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantMonthlyTotalAmount {
    private String reportYear;
    private String monthName;
    private Long totalAmount;
}