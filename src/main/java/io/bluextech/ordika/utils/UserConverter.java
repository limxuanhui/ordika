package io.bluextech.ordika.utils;
/* Created by limxuanhui on 10/3/24 */

import io.bluextech.ordika.models.User;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class UserConverter implements AttributeConverter<User> {

    // Tried to use the bean method to autowire a MediaConverter bean, but it always causes NullPointerException,
    // seemingly because every @DynamoDbConvertedBy results in a UserConverter constructor call, and spring is unaware of
    // this new instance, hence does not inject the MediaConverter bean here.
    private final MediaConverter mediaConverter;

    public UserConverter() {
        System.out.println("UserConverter constructor called");
        this.mediaConverter = new MediaConverter();
    }

    @Override
    public AttributeValue transformFrom(User user) {
        return AttributeValue.fromM(
                Map.of("PK", AttributeValue.fromS(user.getPK()),
                        "SK", AttributeValue.fromS(user.getSK()),
                        "id", AttributeValue.fromS(user.getId()),
                        "name", AttributeValue.fromS(user.getName()),
                        "handle", AttributeValue.fromS(user.getHandle()),
                        "email", AttributeValue.fromS(user.getEmail()),
                        "avatar", mediaConverter.transformFrom(user.getAvatar())
                )
        );
    }

    @Override
    public User transformTo(AttributeValue attributeValue) {
        System.out.println("Running transformTo in UserConverter");
        Map<String, AttributeValue> map = attributeValue.m();
        Map<String, AttributeValue> avatarMap = map.get("avatar").m();
        System.out.println(mediaConverter);
        return new User(
                map.get("id").s(),
                map.get("name").s(),
                map.get("handle").s(),
                map.get("email").s(),
                mediaConverter.transformTo(map.get("avatar"))
        );
    }

    @Override
    public EnhancedType<User> type() {
        return EnhancedType.of(User.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }

}