package io.bluextech.ordika.models;
/* Created by limxuanhui on 10/3/24 */

import io.bluextech.ordika.utils.MediaConverter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Setter
@DynamoDbBean
public class FeedMetadata extends BaseMetadata {

    private Media thumbnail;
    private String taleId;
    private String GSI1PK;
    private String GSI1SK;

    public FeedMetadata() {}

    public FeedMetadata(String PK, String SK, String id, User creator, Media thumbnail, String taleId) {
        super(PK, SK, id, creator);
        this.thumbnail = thumbnail;
        this.taleId = taleId;
    }

    public FeedMetadata(String PK, String SK, String id, User creator, Media thumbnail, String taleId, String GSI1PK, String GSI1SK) {
        super(PK, SK, id, creator);
        this.thumbnail = thumbnail;
        this.taleId = taleId;
        this.GSI1PK = GSI1PK;
        this.GSI1SK = GSI1SK;
    }

    public FeedMetadata(String id, User creator, Media thumbnail, String taleId) {
        super(Feed.USER_PK_PREFIX + creator.getId(), Feed.USER_SK_PREFIX + id, id, creator);
        this.thumbnail = thumbnail;
        this.taleId = taleId;
    }

    public FeedMetadata(String id, User creator, Media thumbnail, String taleId, String GSI1PK, String GSI1SK) {
        super(Feed.PK_PREFIX + id, Feed.SK_PREFIX, id, creator);
        this.thumbnail = thumbnail;
        this.taleId = taleId;
        this.GSI1PK = GSI1PK;
        this.GSI1SK = GSI1SK;
    }

    @DynamoDbConvertedBy(MediaConverter.class)
    @DynamoDbAttribute("thumbnail")
    public Media getThumbnail() {
        return thumbnail;
    }

    @DynamoDbAttribute("taleId")
    public String getTaleId() {
        return taleId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"GSI1"})
    @DynamoDbAttribute(value = "GSI1PK")
    public String getGSI1PK() {
        return GSI1PK;
    }

    @DynamoDbSecondarySortKey(indexNames = {"GSI1"})
    @DynamoDbAttribute(value = "GSI1SK")
    public String getGSI1SK() {
        return GSI1SK;
    }

    @Override
    public String toString() {
        return "FeedMetadata{" +
                "PK='" + this.getPK() + '\'' +
                ", SK='" + this.getSK() + '\'' +
                ", id'" + this.getId() + '\'' +
                ", creator='" + this.getCreator() + '\'' +
                ", thumbnail=" + thumbnail +
                ", taleId='" + taleId + '\'' +
                ", GSI1PK='" + GSI1PK + '\'' +
                ", GSI1SK='" + GSI1SK + '\'' +
                '}';
    }

}