package io.bluextech.ordika.models;
/* Created by limxuanhui on 2/1/24 */

import lombok.Setter;

import java.util.List;

//@Getter(onMethod = @__({
//        @DynamoDbAttribute("snapshot"),
//        @DynamoDbConvertedBy(SnapshotConverter.class)
//}))
@Setter
public class Tale {

    public static final String PK_PREFIX = "TALE#";
    public static final String SK_PREFIX = "#METADATA";
    public static final String SK_PREFIX_ITINERARY = "#METADATA_ITINERARY";
    public static final String SK_PREFIX_STORYITEM = "STORY#STORY_ITEM#";
    public static final String SK_PREFIX_ROUTE = "ITINERARY#ROUTE#";
    public static final String GSI1PK_PREFIX = "METADATA_TALE";
    public static final String GSI1SK_PREFIX = "TALE#";
    public static final String USER_PK_PREFIX = "USER#";
    public static final String USER_SK_PREFIX = "#METADATA_TALE#TALE#";
    private TaleMetadata metadata;
    private Itinerary itinerary;
    private List<StoryItem> story;

    public Tale(TaleMetadata metadata, Itinerary itinerary, List<StoryItem> story) {
        this.metadata = metadata;
        this.itinerary = itinerary;
        this.story = story;
    }

    public TaleMetadata getMetadata() {
        return metadata;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public List<StoryItem> getStory() {
        return story;
    }

    @Override
    public String toString() {
        return "Tale{" +
                "metadata=" + metadata +
                ", itinerary=" + itinerary +
                ", story=" + story +
                '}';
    }

}