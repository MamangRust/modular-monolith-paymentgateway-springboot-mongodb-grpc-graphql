package com.sanedge.paymentgateway.card.domain.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateCardRequest {

    private String cardId;

    private String userId;

    @NotBlank(message = "Card type wajib diisi")
    private String cardType;

    private LocalDate expireDate;

    @NotBlank(message = "CVV wajib diisi")
    private String cvv;

    @NotBlank(message = "Card provider wajib diisi")
    private String cardProvider;
}