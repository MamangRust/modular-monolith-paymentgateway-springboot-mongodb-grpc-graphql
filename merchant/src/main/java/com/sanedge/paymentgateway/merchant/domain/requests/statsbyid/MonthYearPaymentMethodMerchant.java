package com.sanedge.paymentgateway.merchant.domain.requests.statsbyid;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class MonthYearPaymentMethodMerchant {
    private String merchantId;

    @Min(2000)
    @Max(2100)
    private Long year;
}