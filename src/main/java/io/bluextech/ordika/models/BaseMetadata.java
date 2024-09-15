package io.bluextech.ordika.models;
/* Created by limxuanhui on 10/3/24 */

import io.bluextech.ordika.utils.converters.UserConverter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

@Setter
@DynamoDbBean
public class BaseMetadata extends BaseDynamoDbItem {

    private String id;
    private User creator;

    public BaseMetadata() {}

    public BaseMetadata(String PK, String SK, String id, User creator) {
        super(PK, SK);
        this.id = id;
        this.creator = creator;
    }

    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbConvertedBy(UserConverter.class)
    @DynamoDbAttribute("creator")
    public User getCreator() {
        return creator;
    }

    @Override
    public String toString() {
        return "BaseMetadata{" +
                "id='" + id + '\'' +
                ", creator=" + creator +
                '}';
    }

}