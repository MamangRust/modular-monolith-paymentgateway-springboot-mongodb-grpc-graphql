package com.sanedge.paymentgateway.card.domain.responses.stats.balance;

import com.sanedge.paymentgateway.card.models.CardYearBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseYearBalance {
    private String reportYear;
    private Long totalBalance;

    public static CardResponseYearBalance from(CardYearBalance model) {
        return CardResponseYearBalance.builder()
                .reportYear(model.getReportYear())
                .totalBalance(model.getTotalBalance())
                .build();
    }
}
