package com.sanedge.paymentgateway.merchant.domain.requests.statsbyapikey;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MonthYearPaymentMethodApiKey {
    @NotBlank(message = "api_key wajib diisi")
    private String apiKey;

    @Min(value = 2000, message = "Tahun harus antara 2000-2100")
    @Max(value = 2100, message = "Tahun harus antara 2000-2100")
    private Long year;
}
