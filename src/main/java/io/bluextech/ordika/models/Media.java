package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Getter
@Setter
@DynamoDbBean
public class Media {

    private String id;
    private String type;
    private String uri;
    private Integer height;
    private Integer width;

    public Media() {}

    public Media(String id, String type, String uri, Integer height, Integer width) {
        this.id = id;
        this.type = type;
        this.uri = uri;
        this.height = height;
        this.width = width;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", uri='" + uri + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }

}