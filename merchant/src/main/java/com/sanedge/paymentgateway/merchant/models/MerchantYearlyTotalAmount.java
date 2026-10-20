package com.sanedge.paymentgateway.merchant.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantYearlyTotalAmount {
    private String reportYear;
    private Long totalAmount;
}