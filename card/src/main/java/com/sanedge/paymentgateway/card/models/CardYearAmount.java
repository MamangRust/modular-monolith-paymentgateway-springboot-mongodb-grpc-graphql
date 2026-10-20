package com.sanedge.paymentgateway.card.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardYearAmount {
    private String reportYear;
    private Long totalAmount;
}
