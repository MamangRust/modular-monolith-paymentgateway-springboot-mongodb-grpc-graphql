package com.sanedge.paymentgateway.transfer.models;

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
@Document(collection = "transfers")
public class Transfer extends BaseModel {
    @Id
    @Field("transfer_id")
    private String transferId;

    @Indexed(unique = true)
    @Field("transfer_no")
    private String transferNo;       // UUID as String

    @Indexed
    @Field("transfer_from")
    private String transferFrom;

    @Indexed
    @Field("transfer_to")
    private String transferTo;

    @Field("transfer_amount")
    private Long transferAmount;

    @Field("transfer_time")
    private LocalDateTime transferTime;

    @Field("status")
    private Status status = Status.PENDING;
}