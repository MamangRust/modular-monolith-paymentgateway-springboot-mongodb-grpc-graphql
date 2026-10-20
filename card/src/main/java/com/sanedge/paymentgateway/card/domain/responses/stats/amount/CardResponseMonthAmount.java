package com.sanedge.paymentgateway.card.domain.responses.stats.amount;

import com.sanedge.paymentgateway.card.models.CardMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseMonthAmount {
    private String monthName;
    private Long totalAmount;

    public static CardResponseMonthAmount from(CardMonthAmount model) {
        return CardResponseMonthAmount.builder()
                .monthName(model.getMonthName())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}