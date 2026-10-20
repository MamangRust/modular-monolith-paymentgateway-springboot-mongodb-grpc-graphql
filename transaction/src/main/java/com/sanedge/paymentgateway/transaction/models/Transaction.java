package com.sanedge.paymentgateway.transaction.models;

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
@Document(collection = "transactions")
public class Transaction extends BaseModel {
    @Id
    @Field("transaction_id")
    private String transactionId;

    @Indexed(unique = true)
    @Field("transaction_no")
    private String transactionNo;

    @Indexed
    @Field("card_number")
    private String cardNumber;

    @Field("amount")
    private Long amount;

    @Field("payment_method")
    private String paymentMethod;

    @Indexed
    @Field("merchant_id")
    private String merchantId;

    @Field("transaction_time")
    private LocalDateTime transactionTime;

    @Field("status")
    private Status status = Status.PENDING;
}
