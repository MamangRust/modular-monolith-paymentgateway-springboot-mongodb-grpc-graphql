package com.sanedge.paymentgateway.merchant.domain.requests.transactions;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FindAllMerchantTransactionsById {
    private String merchantId;

    @Min(value = 1)
    private Integer page = 1;

    @Min(value = 1)
    private Integer pageSize = 10;

    private String search = "";
}