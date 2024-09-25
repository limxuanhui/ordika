package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 7/1/24 */

import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.models.FeedMetadata;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FeedRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedRepository.class);
    private static final int PAGE_SIZE = 4;
    @Autowired
    private UserService userService;
    @Autowired
    private DynamoDbClient dynamoDbClient;
    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired
    private DynamoDbTable<FeedMetadata> feedMetadataTable;
    @Autowired
    private DynamoDbTable<FeedItem> feedItemTable;

    public Feed getFeedByFeedId(String feedId) {
        FeedMetadata feedMetadata = feedMetadataTable.getItem(Key.builder()
                .partitionValue(Feed.PK_PREFIX + feedId)
                .sortValue(Feed.SK_PREFIX)
                .build());
        List<FeedItem> feedItems = getAllFeedItemsByFeedId(feedId);

        return new Feed(feedMetadata, feedItems);
    }

    public List<FeedItem> getAllFeedItemsByFeedId(String feedId) {
        List<FeedItem> allFeedItems = new ArrayList<>();
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Feed.PK_PREFIX + feedId)
                        .sortValue(Feed.SK_PREFIX_FEED_ITEM)
                        .build()
        );
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .build();
        SdkIterable<FeedItem> feedItemsIterable = feedItemTable.query(request).items();
        feedItemsIterable.forEach(allFeedItems::add);

        return allFeedItems.stream().sorted().toList();
    }

    public List<Feed> getFeedsListByFeedIds(List<String> feedIds) {
        List<Feed> feedList = new ArrayList<>();
        feedIds.forEach(feedId -> {
            feedList.add(getFeedByFeedId(feedId));
        });
        return feedList;
    }

    public List<FeedMetadata> getFeedMetadataListByFeedIds(List<String> feedIds) {
        List<FeedMetadata> feedMetadataList = new ArrayList<>();
        feedIds.forEach(id -> {
            FeedMetadata metadata = feedMetadataTable.getItem(
                    Key.builder()
                            .partitionValue(Feed.PK_PREFIX + id)
                            .sortValue(Feed.SK_PREFIX)
                            .build());
            feedMetadataList.add(metadata);
        });

        return feedMetadataList;
    }

    public Page<Feed> getNextFeedsPage(Map<String, AttributeValue> exclusiveStartKey) {
        QueryConditional feedMetadataPageConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Feed.GSI1PK_PREFIX)
                        .sortValue(Feed.GSI1SK_PREFIX)
                        .build()
        );
        QueryEnhancedRequest feedMetadataPageRequest = QueryEnhancedRequest.builder()
                .queryConditional(feedMetadataPageConditional)
                .limit(PAGE_SIZE)
                .scanIndexForward(false)
                .consistentRead(false)
                .exclusiveStartKey(exclusiveStartKey)
//                .filterExpression(Expression.builder()
//                        .expression("creator.isDeactivated=:isDeactivated")
//                        .expressionValues(Map.of(":isDeactivated", AttributeValue.fromBool(false)))
//                        .build())
                .build();
        long start = System.currentTimeMillis();
        Page<FeedMetadata> feedMetadataPage = feedMetadataTable.index("GSI1")
                .query(feedMetadataPageRequest)
                .stream()
                .limit(1)
                .toList()
                .get(0);
        List<Feed> feeds = new ArrayList<>();
        Set<String> uniqueCreatorIds = feedMetadataPage.items()
                .stream()
                .map(FeedMetadata::getCreatorId)
                .collect(Collectors.toSet());
        List<User> uniqueUsers = userService.getUsersByUserIds(uniqueCreatorIds);

        feedMetadataPage.items().forEach(feedMetadata -> {
            Optional<User> user = uniqueUsers.stream()
                    .filter(u -> Objects.equals(u.getId(), feedMetadata.getCreatorId()))
                    .findFirst();
            if (user.isPresent() && !user.get().getIsDeactivated()) {
                QueryConditional feedItemsConditional = QueryConditional.sortBeginsWith(
                        Key.builder()
                                .partitionValue(feedMetadata.getPK())
                                .sortValue(Feed.SK_PREFIX_FEED_ITEM)
                                .build()
                );

                QueryEnhancedRequest feedItemsRequest = QueryEnhancedRequest.builder()
                        .queryConditional(feedItemsConditional)
                        .scanIndexForward(false)
                        .build();
                List<FeedItem> feedItems = feedItemTable.query(feedItemsRequest)
                        .items()
                        .stream()
                        .sorted()
                        .toList();
                feeds.add(new Feed(feedMetadata, feedItems));
            }
        });

        LOGGER.info("Time taken to get feed page (ms): " + (System.currentTimeMillis() - start));
        Map<String, AttributeValue> lastEvaluatedKey = feedMetadataPage.lastEvaluatedKey();

        return Page.create(feeds, lastEvaluatedKey);
    }

    public Page<Feed> getNextFeedsPageByUserId(String userId) {
        Page<FeedMetadata> feedMetadataPage = getNextFeedsMetadataPageByUserId(userId);
        List<Feed> feedList = new ArrayList<>();
        feedMetadataPage.items().forEach(feedMetadata -> {
            QueryConditional queryConditional = QueryConditional.sortBeginsWith(
                    Key.builder()
                            .partitionValue(Feed.PK_PREFIX + feedMetadata.getId())
                            .sortValue(Feed.SK_PREFIX_FEED_ITEM)
                            .build()
            );
            QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
                    .build();
            List<FeedItem> feedItems = feedItemTable.query(queryEnhancedRequest)
                    .items()
                    .stream()
                    .sorted()
                    .toList();
            FeedMetadata metadata = feedMetadataTable.getItem(
                    Key.builder()
                            .partitionValue(Feed.PK_PREFIX + feedMetadata.getId())
                            .sortValue(Feed.SK_PREFIX)
                            .build()
            );

            feedList.add(new Feed(metadata, feedItems));
        });

        return Page.create(feedList, feedMetadataPage.lastEvaluatedKey());
    }

    public List<Feed> getAllFeedsByUserId(String userId) {
        List<Feed> allFeeds = new ArrayList<>();
        List<FeedMetadata> allFeedMetadataList = getAllFeedsMetadataByUserId(userId);
        allFeedMetadataList.forEach(feedMetadata -> {
            List<FeedItem> feedItems = getAllFeedItemsByFeedId(feedMetadata.getId());
            allFeeds.add(new Feed(feedMetadata, feedItems));
        });

        return allFeeds;
    }

    public Page<FeedMetadata> getNextFeedsMetadataPageByUserId(String userId) {
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Feed.USER_PK_PREFIX + userId)
                        .sortValue(Feed.USER_SK_PREFIX)
                        .build()
        );
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .build();

        // Check if user feed metadata is same schema as regular feed metadata
        Page<FeedMetadata> feedMetadataPage = feedMetadataTable.query(request)
                .stream()
                .limit(1)
                .toList()
                .get(0);
        return feedMetadataPage;
    }

    public List<FeedMetadata> getAllFeedsMetadataByUserId(String userId) {
        List<FeedMetadata> allFeedMetadataList = new ArrayList<>();
        QueryConditional conditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue(Feed.GSI1PK_PREFIX)
                .sortValue(Feed.GSI1SK_PREFIX)
                .build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .filterExpression(Expression.builder()
                        .expression("creatorId=:userId")
                        .expressionValues(ImmutableMap.of(":userId", AttributeValue.fromS(userId)))
                        .build())
                .consistentRead(false) // TODO: should be true since we want to get every feed metadata?
                .build();
        SdkIterable<Page<FeedMetadata>> metadataPageIterable = feedMetadataTable.index("GSI1").query(request);

        metadataPageIterable.forEach(page -> {
            allFeedMetadataList.addAll(page.items());
        });

        return allFeedMetadataList;
    }

    public List<FeedMetadata> getAllUserFeedsMetadataByUserId(String userId) {
        List<FeedMetadata> allFeedMetadataList = new ArrayList<>();
        QueryConditional conditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue(Feed.USER_PK_PREFIX + userId)
                .sortValue(Feed.USER_SK_PREFIX)
                .build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .consistentRead(false) // TODO: should be true since fetching all?
                .build();
        SdkIterable<FeedMetadata> metadataPageIterable = feedMetadataTable.query(request).items();
        metadataPageIterable.forEach(allFeedMetadataList::add);

        return allFeedMetadataList;
    }

    public Feed createNewFeed(Feed feed) {
        FeedMetadata feedMetadata = feed.getMetadata();
        feedMetadata.setPK(Feed.PK_PREFIX + feedMetadata.getId());
        feedMetadata.setSK(Feed.SK_PREFIX);
        feedMetadata.setGSI1PK(Feed.GSI1PK_PREFIX);
        feedMetadata.setGSI1SK(feedMetadata.getPK());

        String creatorId = feedMetadata.getCreatorId();
        FeedMetadata userFeedMetadata = new FeedMetadata(
                Feed.USER_PK_PREFIX + creatorId,
                Feed.USER_SK_PREFIX + feedMetadata.getId(),
                feedMetadata.getId(),
                creatorId,
                feedMetadata.getThumbnail(),
                feedMetadata.getTaleId());

        // TODO: Add feed item media as thumbnail of metadata?
        feedMetadataTable.putItem(userFeedMetadata);
        feedMetadataTable.putItem(feedMetadata);
        feed.getFeedItems().forEach(feedItem -> {
            feedItem.setPK(feedMetadata.getPK());
            feedItem.setSK("FEED_ITEM#" + feedItem.getId());
            feedItem.setFeedId(feedMetadata.getId());
            feedItemTable.putItem(feedItem);
        });
        return feed;
    }

    public BatchWriteResult batchSaveFeedItems(List<FeedItem> feedItems) {
        List<WriteBatch> saveBatches = feedItems
                .stream()
                .map(item -> WriteBatch.builder(FeedItem.class)
                        .mappedTableResource(feedItemTable)
                        .addPutItem(item)
                        .build())
                .toList();

        BatchWriteItemEnhancedRequest batchPutRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(saveBatches)
                .build();

        return dynamoDbEnhancedClient.batchWriteItem(batchPutRequest);
    }

    public FeedMetadata saveFeedMetadata(FeedMetadata feedMetadata) {
        feedMetadataTable.putItem(feedMetadata);
        return feedMetadata;
    }

    public FeedMetadata updateFeedMetadata(FeedMetadata feedMetadata) {
        UpdateItemEnhancedRequest<FeedMetadata> request = UpdateItemEnhancedRequest.builder(FeedMetadata.class)
                .item(feedMetadata)
                .ignoreNulls(true)
                .build();

        return feedMetadataTable.updateItem(request);
    }

    public FeedMetadata updateFeedMetadata(FeedMetadata feedMetadata, Boolean ignoreNulls) {
        UpdateItemEnhancedRequest<FeedMetadata> request = UpdateItemEnhancedRequest.builder(FeedMetadata.class)
                .item(feedMetadata)
                .ignoreNulls(ignoreNulls)
                .build();

        return feedMetadataTable.updateItem(request);
    }


    public List<FeedMetadata> batchUpdateFeedMetadata(List<FeedMetadata> feedMetadataList) {
        LOGGER.info("Batch updating feed metadata");
        List<FeedMetadata> updatedFeedMetadataList = new ArrayList<>();
        feedMetadataList.forEach(feedMetadata -> {
            FeedMetadata updatedMetadata = updateFeedMetadata(feedMetadata);
            updatedFeedMetadataList.add(updatedMetadata);
        });

        return updatedFeedMetadataList;
    }

    public List<FeedItem> batchUpdateFeedItems(List<FeedItem> feedItems) {
        LOGGER.info("Batch updating feed items");
        feedItems.forEach(feedItem -> {
            FeedItem updatedFeedItem = feedItemTable.updateItem(feedItem);
        });

        return feedItems;
    }

    public BatchWriteResult batchDeleteFeedItems(List<FeedItem> feedItems) {
        List<WriteBatch> deleteBatches = feedItems
                .stream()
                .map(item -> WriteBatch.builder(FeedItem.class)
                        .mappedTableResource(feedItemTable)
                        .addDeleteItem(
                                Key.builder()
                                        .partitionValue(item.getPK())
                                        .sortValue(item.getSK())
                                        .build()
                        ).build())
                .toList();

        BatchWriteItemEnhancedRequest batchDeleteRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(deleteBatches)
                .build();

        return dynamoDbEnhancedClient.batchWriteItem(batchDeleteRequest);
    }

    public List<FeedMetadata> updateFeedsTaleId(List<FeedMetadata> feedMetadataList) {
        feedMetadataList.forEach(feedMetadata -> {
            feedMetadata.setId(null);
            feedMetadata.setCreatorId(null);
            feedMetadata.setThumbnail(null);
            feedMetadata.setGSI1PK(null);
            feedMetadata.setGSI1SK(null);
            UpdateItemEnhancedRequest<FeedMetadata> request = UpdateItemEnhancedRequest.builder(FeedMetadata.class)
                    .item(feedMetadata)
                    .ignoreNulls(true)
                    .build();
            feedMetadataTable.updateItem(request);
        });

        return getFeedMetadataListByFeedIds(feedMetadataList.stream().map(FeedMetadata::getId).toList());
    }

    public Feed deleteFeed(Feed feed) {
        FeedMetadata userFeedMetadata = new FeedMetadata(
                Feed.USER_PK_PREFIX + feed.getMetadata().getCreatorId(),
                Feed.USER_SK_PREFIX + feed.getMetadata().getId(),
                feed.getMetadata().getId(),
                feed.getMetadata().getCreatorId(),
                feed.getMetadata().getThumbnail(),
                feed.getMetadata().getTaleId());
        feedMetadataTable.deleteItem(userFeedMetadata);

        DeleteItemEnhancedRequest metadataRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(feed.getMetadata().getPK())
                        .sortValue(feed.getMetadata().getSK())
                        .build())
                .build();
        FeedMetadata deletedMetadata = feedMetadataTable.deleteItem(metadataRequest);
        LOGGER.info("Deleted metadata: " + deletedMetadata);

        // Check if index entries are deleted?

//        // Get list of feed item id, then delete them
//        QueryConditional conditional = QueryConditional.sortBeginsWith(
//                Key.builder()
//                        .partitionValue(feed.getMetadata().getPK())
//                        .sortValue("FEED_ITEM#")
//                        .build()
//        );
//        QueryEnhancedRequest feedItemGetRequest = QueryEnhancedRequest.builder()
//                .queryConditional(conditional)
//                .scanIndexForward(false)
//                .build();
//        List<FeedItem> feedItemsList = feedItemTable.query(feedItemGetRequest)
//                .items()
//                .stream()
//                .toList();

        BatchWriteResult batchDeleteResult = batchDeleteFeedItems(feed.getFeedItems());
        LOGGER.info("Deleted feed items: " + batchDeleteResult);

        // Check unprocessed items, and try to delete again
        return feed;
    }


//    // WriteFeedScreen (edit)
//    public Feed updateFeed(Feed feed) {
//        FeedMetadata userFeedMetadata = new FeedMetadata(
//                feed.getMetadata().getCreator().getPK(),
//                "#METADATA_FEED#" + feed.getMetadata().getId(),
//                feed.getMetadata().getId(),
//                feed.getMetadata().getCreator(),
//                feed.getMetadata().getThumbnail(),
//                feed.getMetadata().getTaleId());
//        feedMetadataTable.updateItem(userFeedMetadata);
//        feedMetadataTable.updateItem(feed.getMetadata());
//        feed.getFeedItems().forEach(feedItem -> {
//            feedItemTable.updateItem(feedItem);
//
//            // Check if feed.getItems() has any items deleted compared to db, and delete those from db.
//        });
//        return feed;
//    }

}