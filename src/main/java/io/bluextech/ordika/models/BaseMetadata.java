package io.bluextech.ordika.models;
/* Created by limxuanhui on 10/3/24 */

import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@NoArgsConstructor
@Setter
@DynamoDbBean
public class BaseMetadata extends BaseDynamoDbItem {

    private String id;
    private String creatorId;

    public BaseMetadata(String PK, String SK, String id, String creatorId) {
        super(PK, SK);
        this.id = id;
        this.creatorId = creatorId;
    }

    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("creatorId")
    public String getCreatorId() {
        return creatorId;
    }

    @Override
    public String toString() {
        return "BaseMetadata{" +
                "id='" + id + '\'' +
                ", creatorId=" + creatorId +
                '}';
    }

}