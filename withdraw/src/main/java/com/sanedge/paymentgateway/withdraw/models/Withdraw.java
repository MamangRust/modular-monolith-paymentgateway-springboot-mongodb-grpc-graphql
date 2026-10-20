package com.sanedge.paymentgateway.withdraw.models;

import java.time.LocalDateTime;

import com.sanedge.paymentgateway.common.enums.Status;
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
@Document(collection = "withdraws")
public class Withdraw extends BaseModel {
    @Id
    private String withdrawId;

    @Indexed(unique = true)
    @Field("withdraw_no")
    private String withdrawNo;    

    @Indexed
    @Field("card_number")
    private String cardNumber;

    @Field("withdraw_amount")
    private Long withdrawAmount;

    @Field("withdraw_time")
    private LocalDateTime withdrawTime;

    @Field("status")
    private Status status = Status.PENDING;
}