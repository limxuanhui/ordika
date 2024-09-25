package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import io.bluextech.ordika.utils.converters.MediaConverter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

@NoArgsConstructor
@Setter
@DynamoDbBean
public class FeedItem extends BaseDynamoDbItem implements Comparable<FeedItem> {

    private String id;
    private Media thumbnail;
    private Media media;
    private String caption;
    private String feedId;
    private Integer order;

    public FeedItem(String PK, String SK, String id, Media thumbnail, Media media, String caption, String feedId) {
        super(PK, SK);
        this.id = id;
        this.thumbnail = thumbnail;
        this.media = media;
        this.caption = caption;
        this.feedId = feedId;
    }

    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbConvertedBy(MediaConverter.class)
    @DynamoDbAttribute("thumbnail")
    public Media getThumbnail() {
        return thumbnail;
    }

    @DynamoDbConvertedBy(MediaConverter.class)
    @DynamoDbAttribute("media")
    public Media getMedia() {
        return media;
    }

    @DynamoDbAttribute("caption")
    public String getCaption() {
        return caption;
    }

    @DynamoDbAttribute("feedId")
    public String getFeedId() {
        return feedId;
    }

    @DynamoDbAttribute("order")
    public Integer getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "PK='" + this.getPK() + '\'' +
                ", SK='" + this.getSK() + '\'' +
                ", id='" + id + '\'' +
                ", thumbnail=" + thumbnail +
                ", media=" + media +
                ", caption='" + caption + '\'' +
                ", feedId='" + feedId + '\'' +
                ", order='" + order + '\'' +
                '}';
    }

    @Override
    public int compareTo(FeedItem o) {
        return this.order.compareTo(o.getOrder());
    }

}