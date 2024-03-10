package io.bluextech.ordika.utils;
/* Created by limxuanhui on 10/3/24 */

import io.bluextech.ordika.models.Media;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class MediaConverter implements AttributeConverter<Media> {

    public MediaConverter() {
        System.out.println("MediaConverter constructor called");
    }

    @Override
    public AttributeValue transformFrom(Media media) {
        return AttributeValue.fromM(
                Map.of(
                        "id", AttributeValue.fromS(media.getId()),
                        "type", AttributeValue.fromS(media.getType()),
                        "uri", AttributeValue.fromS(media.getUri()),
                        "height", AttributeValue.fromN(media.getHeight().toString()),
                        "width", AttributeValue.fromN(media.getWidth().toString())
                )
        );
    }

    @Override
    public Media transformTo(AttributeValue attributeValue) {
        Map<String, AttributeValue> map = attributeValue.m();
        return new Media(
                map.get("id").s(),
                map.get("type").s(),
                map.get("uri").s(),
                Integer.valueOf(map.get("height").n()),
                Integer.valueOf(map.get("width").n())
        );
    }

    @Override
    public EnhancedType<Media> type() {
        return EnhancedType.of(Media.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }

}