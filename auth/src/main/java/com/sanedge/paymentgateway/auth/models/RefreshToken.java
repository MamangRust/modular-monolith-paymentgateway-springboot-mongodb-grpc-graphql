package com.sanedge.paymentgateway.auth.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.sanedge.paymentgateway.common.models.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "refresh_tokens")
public class RefreshToken extends BaseModel {
    @Id
    @Field("refresh_token_id")
    private String refreshTokenId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed(unique = true)
    @Field("token")
    private String token;

    @Field("expiration")
    private LocalDateTime expiration;
}