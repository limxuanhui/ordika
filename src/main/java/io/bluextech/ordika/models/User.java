package io.bluextech.ordika.models;
/* Created by limxuanhui on 13/7/23 */

import io.bluextech.ordika.utils.MediaConverter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Setter
@DynamoDbBean
public class User {

    public static final String PK_PREFIX = "USER#";
    public static final String SK_PREFIX = "METADATA#";
    private String PK;
    private String SK;
    private String id;
    private String name;
    private String handle;
    private String email;
    private Media avatar;

    public User() {}

    public User(String id, String name, String handle, String email, Media avatar) {
        this.PK = PK_PREFIX + id;
        this.SK = SK_PREFIX;
        this.id = id;
        this.name = name;
        this.handle = handle;
        this.email = email;
        this.avatar = avatar;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "PK")
    public String getPK() {
        return PK;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = "SK")
    public String getSK() {
        return SK;
    }

    @DynamoDbAttribute(value = "id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute(value = "name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute(value = "handle")
    public String getHandle() {
        return handle;
    }

    @DynamoDbAttribute(value = "email")
    public String getEmail() {
        return email;
    }

    @DynamoDbConvertedBy(MediaConverter.class)
    @DynamoDbAttribute(value = "avatar")
    public Media getAvatar() {
        return avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "PK='" + PK + '\'' +
                ", SK='" + SK + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", handle='" + handle + '\'' +
                ", email='" + email + '\'' +
                ", avatar=" + avatar +
                '}';
    }

}
