package io.bluextech.ordika.models;
/* Created by limxuanhui on 11/5/24 */

import io.bluextech.ordika.utils.converters.InstantConverter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Setter
@DynamoDbBean
public class UserDeletionInfo extends BaseDynamoDbItem {

    public static final String PK_PREFIX = "USER_DELETION";
    public static final String SK_PREFIX = "USER#";
    private String userId;
    private Instant deactivationDate;
    private Instant deletionDate;

    public UserDeletionInfo() {}

    public UserDeletionInfo(String userId, Instant deactivationDate) {
        super(PK_PREFIX, SK_PREFIX + userId);
        this.userId = userId;
        this.deactivationDate = deactivationDate;
        this.deletionDate = deactivationDate.plus(30, ChronoUnit.DAYS);
    }

    @DynamoDbAttribute("userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDbAttribute("deactivationDate")
    public Instant getDeactivationDate() {
        return deactivationDate;
    }

    @DynamoDbConvertedBy(InstantConverter.class)
    @DynamoDbAttribute("deletionDate")
    public Instant getDeletionDate() {
        return deletionDate;
    }

}
