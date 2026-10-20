package com.sanedge.paymentgateway.merchant.models;

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
@Document(collection = "merchants")
public class Merchant extends BaseModel {
    @Id
    private String merchantId;

    @Indexed(unique = true)
    @Field("merchant_no")
    private String merchantNo;

    @Field("name")
    private String name;

    @Indexed(unique = true)
    @Field("api_key")
    private String apiKey;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("status")
    private Status status = Status.PENDING;
}
