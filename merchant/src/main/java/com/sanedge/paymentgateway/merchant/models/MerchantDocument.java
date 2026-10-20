package com.sanedge.paymentgateway.merchant.models;

import com.sanedge.paymentgateway.common.models.BaseModel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Merchant verification document (collection {@code merchant_documents}).
 *
 * <p>MongoDB port of the blueprint's {@code MerchantDocument} entity. Ids are
 * Strings (generated like the rest of the payment-gateway collections) instead
 * of the JPA {@code @GeneratedValue} Long id.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "merchant_documents")
public class MerchantDocument extends BaseModel {

    @Id
    private String documentId;

    @Indexed
    @Field("merchant_id")
    private String merchantId;

    @Field("document_type")
    private String documentType;

    @Field("document_url")
    private String documentUrl;

    @Indexed
    @Field("status")
    private String status = "PENDING";

    @Field("note")
    private String note;
}
