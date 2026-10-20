package com.sanedge.paymentgateway.user.models;

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
 * Join collection between users and roles. In the modular architecture the owning
 * role lives in another service, so relations are stored as raw id fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user_roles")
public class UserRole extends BaseModel {

    @Id
    @Field("user_role_id")
    private String userRoleId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Indexed
    @Field("role_id")
    private String roleId;
}
