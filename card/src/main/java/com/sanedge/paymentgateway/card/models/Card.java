package com.sanedge.paymentgateway.card.models;

import java.time.LocalDate;

import com.sanedge.paymentgateway.common.models.BaseModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "cards")
public class Card extends BaseModel {

    @Id
    private String cardId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed(unique = true)
    @Field("card_number")
    private String cardNumber;

    @Field("card_type")
    private String cardType;

    @Field("expire_date")
    private LocalDate expireDate;

    @Field("cvv")
    private String cvv;

    @Field("card_provider")
    private String cardProvider;
}
