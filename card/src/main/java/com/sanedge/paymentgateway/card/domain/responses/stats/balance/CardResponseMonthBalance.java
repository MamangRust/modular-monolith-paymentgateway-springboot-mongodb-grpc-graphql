package com.sanedge.paymentgateway.card.domain.responses.stats.balance;

import com.sanedge.paymentgateway.card.models.CardMonthBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseMonthBalance {
    private String monthName;
    private Long totalBalance;

    public static CardResponseMonthBalance from(CardMonthBalance model) {
        return CardResponseMonthBalance.builder()
                .monthName(model.getMonthName())
                .totalBalance(model.getTotalBalance())
                .build();
    }
}