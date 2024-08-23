package io.bluextech.ordika.utils.converters;
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
    private final InstantConverter instantConverter;

    public UserConverter() {
        System.out.println("UserConverter constructor called");
        this.mediaConverter = new MediaConverter();
        this.instantConverter = new InstantConverter();
    }

    @Override
    public AttributeValue transformFrom(User user) {
        if (user.getCreatedAt() != null) {
            return AttributeValue.fromM(
                    Map.of("PK", AttributeValue.fromS(user.getPK()),
                            "SK", AttributeValue.fromS(user.getSK()),
                            "id", AttributeValue.fromS(user.getId()),
                            "name", AttributeValue.fromS(user.getName()),
                            "handle", AttributeValue.fromS(user.getHandle()),
                            "email", AttributeValue.fromS(user.getEmail()),
                            "avatar", mediaConverter.transformFrom(user.getAvatar()),
                            "createdAt", instantConverter.transformFrom(user.getCreatedAt()),
                            "isDeactivated", AttributeValue.fromBool(user.getIsDeactivated())
                    )
            );
        } else {
            return AttributeValue.fromM(
                    Map.of("PK", AttributeValue.fromS(user.getPK()),
                            "SK", AttributeValue.fromS(user.getSK()),
                            "id", AttributeValue.fromS(user.getId()),
                            "name", AttributeValue.fromS(user.getName()),
                            "handle", AttributeValue.fromS(user.getHandle()),
                            "email", AttributeValue.fromS(user.getEmail()),
                            "avatar", mediaConverter.transformFrom(user.getAvatar()),
                            "isDeactivated", AttributeValue.fromBool(user.getIsDeactivated())
                    )
            );
        }
    }

    @Override
    public User transformTo(AttributeValue attributeValue) {
        Map<String, AttributeValue> map = attributeValue.m();
        return new User(
                map.get("id").s(),
                map.get("name").s(),
                map.get("handle").s(),
                map.get("email").s(),
                mediaConverter.transformTo(map.get("avatar")),
                instantConverter.transformTo(map.get("createdAt")),
                map.get("isDeactivated").bool()
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