package com.sanedge.paymentgateway.topup.models;

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
@Document(collection = "topups")
public class Topup extends BaseModel {

    @Id
    private String topupId;

    @Indexed(unique = true)
    @Field("topup_no")
    private String topupNo;          // UUID as String

    @Indexed
    @Field("card_number")
    private String cardNumber;

    @Field("topup_amount")
    private Long topupAmount;

    @Field("topup_method")
    private String topupMethod;

    @Field("topup_time")
    private LocalDateTime topupTime;

    @Field("status")
    private Status status = Status.PENDING;
}
