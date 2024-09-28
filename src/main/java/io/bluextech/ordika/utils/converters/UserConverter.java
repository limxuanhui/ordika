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
    // seemingly because every @DynamoDbConvertedBy results in a UserConverter constructor call,
    // and spring is unaware of this new instance, hence does not inject the MediaConverter bean here.
    private final MediaConverter mediaConverter;
    private final InstantConverter instantConverter;

    public UserConverter() {
        this.mediaConverter = new MediaConverter();
        this.instantConverter = new InstantConverter();
    }

    @Override
    public AttributeValue transformFrom(User user) {
        // Existing user
        if (user.getCreatedAt() != null) {
            Map<String, AttributeValue> userMap = Map.ofEntries(
                    Map.entry("PK", AttributeValue.fromS(user.getPK())),
                    Map.entry("SK", AttributeValue.fromS(user.getSK())),
                    Map.entry("id", AttributeValue.fromS(user.getId())),
                    Map.entry("name", AttributeValue.fromS(user.getName())),
                    Map.entry("handle", AttributeValue.fromS(user.getHandle())),
                    Map.entry("email", AttributeValue.fromS(user.getEmail())),
                    Map.entry("bio", AttributeValue.fromS(user.getBio() != null ? user.getBio() : "")),
                    Map.entry("avatar", mediaConverter.transformFrom(user.getAvatar())),
                    Map.entry("createdAt", instantConverter.transformFrom(user.getCreatedAt())),
                    Map.entry("lastUpdatedNameAt",
                            user.getLastUpdatedNameAt() != null
                                    ? instantConverter.transformFrom(user.getLastUpdatedNameAt())
                                    : AttributeValue.fromNul(true)),
                    Map.entry("lastUpdatedHandleAt",
                            user.getLastUpdatedHandleAt() != null
                                    ? instantConverter.transformFrom(user.getLastUpdatedHandleAt())
                                    : AttributeValue.fromNul(true)),
                    Map.entry("lastUpdatedBioAt",
                            user.getLastUpdatedBioAt() != null
                                    ? instantConverter.transformFrom(user.getLastUpdatedBioAt())
                                    : AttributeValue.fromNul(true)),
                    Map.entry("lastUpdatedAvatarAt",
                            user.getLastUpdatedBioAt() != null
                                    ? instantConverter.transformFrom(user.getLastUpdatedAvatarAt())
                                    : AttributeValue.fromNul(true)),
                    Map.entry("isDeactivated", AttributeValue.fromBool(user.getIsDeactivated()))
            );
            return AttributeValue.fromM(userMap);
        } else {
            return AttributeValue.fromM(
                    Map.of("PK", AttributeValue.fromS(user.getPK()),
                            "SK", AttributeValue.fromS(user.getSK()),
                            "id", AttributeValue.fromS(user.getId()),
                            "name", AttributeValue.fromS(user.getName()),
                            "handle", AttributeValue.fromS(user.getHandle()),
                            "email", AttributeValue.fromS(user.getEmail()),
                            "bio", AttributeValue.fromS(user.getBio()),
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
                map.containsKey("bio") && map.get("lastUpdatedNameAt").s() != null
                        ? map.get("bio").s() : "",
                mediaConverter.transformTo(map.get("avatar")),
                instantConverter.transformTo(map.get("createdAt")),
                map.containsKey("lastUpdatedNameAt") && map.get("lastUpdatedNameAt").s() != null
                        ? instantConverter.transformTo(map.get("lastUpdatedNameAt")) : null,
                map.containsKey("lastUpdatedHandleAt") && map.get("lastUpdatedHandleAt").s() != null
                        ? instantConverter.transformTo(map.get("lastUpdatedHandleAt")) : null,
                map.containsKey("lastUpdatedBioAt") && map.get("lastUpdatedBioAt").s() != null
                        ? instantConverter.transformTo(map.get("lastUpdatedBioAt")) : null,
                map.containsKey("lastUpdatedAvatarAt") && map.get("lastUpdatedAvatarAt").s() != null
                        ? instantConverter.transformTo(map.get("lastUpdatedAvatarAt")) : null,
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