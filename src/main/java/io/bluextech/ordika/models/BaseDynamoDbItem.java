package io.bluextech.ordika.models;
/* Created by limxuanhui on 10/3/24 */

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@DynamoDbBean
public class BaseDynamoDbItem {

    private String PK;
    private String SK;

    public BaseDynamoDbItem() {}

    public BaseDynamoDbItem(String PK, String SK) {
        this.PK = PK;
        this.SK = SK;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPK() {
        return PK;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSK() {
        return SK;
    }

    @Override
    public String toString() {
        return "BaseDynamoDbItem{" +
                "PK='" + PK + '\'' +
                ", SK='" + SK + '\'' +
                '}';
    }

}