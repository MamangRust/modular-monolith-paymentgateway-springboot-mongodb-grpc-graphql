package com.sanedge.paymentgateway.saldo.models;

import java.time.LocalDateTime;

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
@Document(collection = "saldos")
public class Saldo extends BaseModel {
    @Id
    @Field("saldo_id")
    private String saldoId;

    @Indexed
    @Field("card_number")
    private String cardNumber;

    @Field("total_balance")
    private Long totalBalance;

    @Field("withdraw_amount")
    private Long withdrawAmount;

    @Field("withdraw_time")
    private LocalDateTime withdrawTime;
}
