package io.bluextech.ordika.models;
/* Created by limxuanhui on 3/1/24 */

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.Map;

@Setter
@DynamoDbBean
public class StoryItem extends BaseDynamoDbItem implements Comparable<StoryItem> {

    private String id;
    private Integer type;
    private Map<String, String> data;
    private Integer order;

    public StoryItem() {}

    public StoryItem(String id, Integer type, Map<String, String> data, Integer order) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.order = order;
    }

    @DynamoDbAttribute(value = "id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute(value = "type")
    public Integer getType() {
        return type;
    }

    @DynamoDbAttribute(value = "order")
    public Integer getOrder() {
        return order;
    }

    @DynamoDbAttribute(value = "data")
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "StoryItem{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                ", order=" + order +
                '}';
    }

    @Override
    public int compareTo(StoryItem o) {
        return this.order.compareTo(o.getOrder());
    }

}