package io.bluextech.ordika.utils;
/* Created by limxuanhui on 8/5/24 */

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;

public class InstantConverter implements AttributeConverter<Instant> {

    public InstantConverter() {
        System.out.println("InstantConverter constructor called");
    }


    @Override
    public AttributeValue transformFrom(Instant instant) {
        return AttributeValue.fromS(instant.toString());
    }

    @Override
    public Instant transformTo(AttributeValue attributeValue) {
        return Instant.parse(attributeValue.s());
    }

    @Override
    public EnhancedType<Instant> type() {
        return EnhancedType.of(Instant.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}
