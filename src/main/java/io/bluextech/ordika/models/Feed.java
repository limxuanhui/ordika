package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Setter;

import java.util.List;

@Setter
public class Feed {

    public static final String PK_PREFIX = "FEED#";
    public static final String SK_PREFIX = "#METADATA";
    public static final String SK_PREFIX_FEED_ITEM = "FEED_ITEM#";
    public static final String GSI1PK_PREFIX = "METADATA_FEED";
    public static final String GSI1SK_PREFIX = "FEED#";
    public static final String USER_PK_PREFIX = "USER#";
    public static final String USER_SK_PREFIX = "#METADATA_FEED#FEED#";
    private FeedMetadata metadata;
    private List<FeedItem> feedItems;

    public Feed(FeedMetadata metadata, List<FeedItem> feedItems) {
        this.metadata = metadata;
        this.feedItems = feedItems;
    }

    public FeedMetadata getMetadata() {
        return metadata;
    }

    public List<FeedItem> getFeedItems() {
        return feedItems;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "metadata=" + metadata +
                ", feedItems=" + feedItems +
                '}';
    }

}