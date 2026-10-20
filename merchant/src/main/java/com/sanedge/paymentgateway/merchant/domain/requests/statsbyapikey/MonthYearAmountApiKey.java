package com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MonthYearAmountApiKey {
    @NotBlank(message = "api_key wajib diisi")
    private String apiKey;

    @Min(2000)
    @Max(2100)
    private Long year;
}