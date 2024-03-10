package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 7/1/24 */

import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.models.FeedMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.List;

@Repository
public class FeedRepository {

    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired
    private DynamoDbTable<FeedMetadata> feedMetadataTable;
    @Autowired
    private DynamoDbTable<FeedItem> feedItemTable;

    // ProfileScreen, MyFeeds, paginated feeds metadata
    public PageIterable<FeedMetadata> getFeedsMetadataByUserId(String userId) {
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("USER#" + userId)
                        .sortValue("#METADATA_TALE#")
                        .build()
        );

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .build();

        PageIterable<FeedMetadata> feedsMetadata = feedMetadataTable.query(request);
        System.out.println(feedsMetadata);
        return feedsMetadata;
    }

    // HomeScreen, paginated feeds
    public Page<Feed> getFeedsPage(Integer cursor) {
        return null;
    }

    // FeedScreen -> WriteFeedScreen (for edit)
    public Feed getFeedByFeedId(String feedId) {
        FeedMetadata feedMetadata = feedMetadataTable.getItem(Key.builder()
                .partitionValue("FEED#" + feedId)
                .sortValue("#METADATA")
                .build());
        System.out.println(feedMetadata);
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("FEED#" + feedId)
                        .sortValue("FEED_ITEM#")
                        .build()
        );
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .build();
        List<FeedItem> feedItems = feedItemTable.query(request).items().stream().toList();
        System.out.println("FEEDItems\n" + feedItems);
        Feed feed = new Feed(feedMetadata, feedItems);
        System.out.println(feed);
        return feed;
    }

    // WriteFeedScreen (new)
    public Feed createNewFeed(Feed feed) {
        feedMetadataTable.putItem(feed.getMetadata());
        for (FeedItem feedItem : feed.getFeedItems()) {
            feedItemTable.putItem(feedItem);
        }
        return feed;
    }

    // WriteFeedScreen (edit)
    public Feed updateFeed(Feed feed) {
        feedMetadataTable.putItem(feed.getMetadata());
        return feed;
    }

    // WriteFeedScreen (delete)
    public String deleteFeedByFeedId(String feedId) {
        // Delete feed metadata from user partition
//        DeleteItemEnhancedRequest userMetadataRequest = DeleteItemEnhancedRequest.builder()
//                .key(Key.builder()
//                        .partitionValue("USER#" + feedId)
//                        .sortValue("#METADATA_FEED#" + feedId)
//                        .build())
//                .build();

//        FeedMetadata deletedMetadata = feedMetadataTable.deleteItem(metadataRequest);
//        System.out.println("deleted metadata");
//        System.out.println(deletedMetadata);

//        TransactWriteItemsEnhancedRequest.builder().

        DeleteItemEnhancedRequest metadataRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue("FEED#" + feedId)
                        .sortValue("#METADATA")
                        .build())
                .build();
        FeedMetadata deletedMetadata = feedMetadataTable.deleteItem(metadataRequest);
        System.out.println("deleted metadata");
        System.out.println(deletedMetadata);
        // Check if index entries are deleted?

        // Get list of feed item id, then delete them
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("FEED#" + feedId)
                        .sortValue("FEED_ITEM#")
                        .build()
        );
        QueryEnhancedRequest feedItemGetRequest = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .scanIndexForward(false)
                .build();
        List<FeedItem> feedItemsList = feedItemTable.query(feedItemGetRequest)
                .items()
                .stream()
                .toList();

        List<WriteBatch> deleteBatches = feedItemsList.stream()
                .map(item -> WriteBatch.builder(FeedItem.class)
                        .mappedTableResource(feedItemTable)
                        .addDeleteItem(Key.builder()
                                .partitionValue(item.getPK())
                                .sortValue(item.getSK())
                                .build())
                        .build())
                .toList();
        BatchWriteItemEnhancedRequest batchDeleteRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(deleteBatches)
                .build();
        BatchWriteResult batchDeleteResult = dynamoDbEnhancedClient.batchWriteItem(batchDeleteRequest);
        System.out.println("Deleted feed items");
        System.out.println(batchDeleteResult);

        // Check unprocessed items, and try to delete again
        return feedId;
    }


}