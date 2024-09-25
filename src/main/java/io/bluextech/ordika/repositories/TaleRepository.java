package io.bluextech.ordika.repositories;

import io.bluextech.ordika.dto.UpdateTaleRequestBody;
import io.bluextech.ordika.models.*;
import io.bluextech.ordika.services.FeedService;
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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TaleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaleRepository.class);
    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired
    private UserService userService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private DynamoDbTable<TaleMetadata> taleMetadataTable;
    @Autowired
    private DynamoDbTable<BaseMetadata> baseMetadataTable;
    @Autowired
    private DynamoDbTable<Route> routeTable;
    @Autowired
    private DynamoDbTable<StoryItem> storyItemTable;

    public List<TaleMetadata> batchUpdateTaleMetadata(List<TaleMetadata> taleMetadataList) {
        List<TaleMetadata> updatedTaleMetadataList = new ArrayList<>();
        taleMetadataList.forEach(taleMetadata -> {
            TaleMetadata updatedTaleMetadata = updateTaleMetadata(taleMetadata);
            updatedTaleMetadataList.add(updatedTaleMetadata);
        });

        return updatedTaleMetadataList;
    }

    public List<BaseMetadata> batchUpdateItineraryMetadata(List<BaseMetadata> itineraryMetadataList) {
        List<BaseMetadata> updatedItineraryMetadataList = new ArrayList<>();
        itineraryMetadataList.forEach(itineraryMetadata -> {
            BaseMetadata updatedItineraryMetadata = updateBaseMetadata(itineraryMetadata);
            updatedItineraryMetadataList.add(updatedItineraryMetadata);
        });

        return updatedItineraryMetadataList;
    }

    public BatchWriteResult batchSaveRoutes(List<Route> routes) {
        List<WriteBatch> saveBatches = routes.stream()
                .map(item -> WriteBatch.builder(Route.class)
                        .mappedTableResource(routeTable)
                        .addPutItem(item)
                        .build())
                .toList();

        BatchWriteItemEnhancedRequest batchPutRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(saveBatches)
                .build();

        return dynamoDbEnhancedClient.batchWriteItem(batchPutRequest);
    }

    public List<Route> batchUpdateRoutes(List<Route> routes) {
        LOGGER.info("Batch updating routes...");
        routes.forEach(route -> {
            Route updatedRoute = routeTable.updateItem(route);
            LOGGER.trace("Updated route: " + updatedRoute.getId());
        });

        return routes;
    }

    public BatchWriteResult batchDeleteRoutes(String taleId, List<String> routeIds) {
        List<WriteBatch> deleteBatches = routeIds
                .stream()
                .map(routeId -> WriteBatch.builder(Route.class)
                        .mappedTableResource(routeTable)
                        .addDeleteItem(
                                Key.builder()
                                        .partitionValue("TALE#" + taleId)
                                        .sortValue("ITINERARY#ROUTE#" + routeId)
                                        .build()
                        ).build())
                .toList();

        BatchWriteItemEnhancedRequest batchDeleteRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(deleteBatches)
                .build();

        return dynamoDbEnhancedClient.batchWriteItem(batchDeleteRequest);
    }

    public List<StoryItem> batchGetStoryItems(String taleId, List<String> storyItemIds) {
        ReadBatch.Builder<StoryItem> getBatchBuilder = ReadBatch.builder(StoryItem.class)
                .mappedTableResource(storyItemTable);
        storyItemIds.forEach(id -> {
            getBatchBuilder.addGetItem(Key.builder()
                    .partitionValue("TALE#" + taleId)
                    .sortValue("STORY#STORY_ITEM#" + id)
                    .build());
        });
        ReadBatch readBatch = getBatchBuilder.build();
        BatchGetItemEnhancedRequest request = BatchGetItemEnhancedRequest.builder()
                .addReadBatch(readBatch)
                .build();
        BatchGetResultPageIterable pageIterable = dynamoDbEnhancedClient.batchGetItem(request);
        return pageIterable.resultsForTable(storyItemTable).stream().toList();
    }

    public List<StoryItem> batchUpdateStoryItems(List<StoryItem> storyItems) {
        LOGGER.info("Batch updating story items...");
        storyItems.forEach(storyItem -> {
            StoryItem updatedStoryItem = storyItemTable.updateItem(storyItem);
            LOGGER.trace("Updated story item: " + updatedStoryItem.getId());
        });

        return storyItems;
    }

    public BatchWriteResult batchDeleteStoryItems(String taleId, List<String> storyItemIds) {
        long start = System.currentTimeMillis();
        WriteBatch.Builder<StoryItem> deleteBatchBuilder = WriteBatch.builder(StoryItem.class)
                .mappedTableResource(storyItemTable);
        storyItemIds.forEach(id -> {
            deleteBatchBuilder.addDeleteItem(Key.builder()
                    .partitionValue("TALE#" + taleId)
                    .sortValue("STORY#STORY_ITEM#" + id)
                    .build());
        });
        WriteBatch deleteBatch = deleteBatchBuilder.build();
        BatchWriteItemEnhancedRequest batchDeleteRequest = BatchWriteItemEnhancedRequest.builder()
                .addWriteBatch(deleteBatch)
                .build();
        BatchWriteResult batchDeleteResult = dynamoDbEnhancedClient.batchWriteItem(batchDeleteRequest);
        LOGGER.info("Time taken to batch delete story items (ms): " + (System.currentTimeMillis() - start));
        return batchDeleteResult;
    }

    public Page<TaleMetadata> getNextTalesMetadataPage(Map<String, AttributeValue> exclusiveStartKey) {
        // Check if there are anymore metadata pages to fetch,
        // if not log "No more new tales metadata"
        QueryConditional conditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue(Tale.GSI1PK_PREFIX)
                .sortValue(Tale.GSI1SK_PREFIX)
                .build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .consistentRead(false)
                .exclusiveStartKey(exclusiveStartKey)
//                .filterExpression(Expression.builder()
//                        .expression("creator.isDeactivated=:isDeactivated")
//                        .expressionValues(Map.of(":isDeactivated", AttributeValue.fromBool(false)))
//                        .build())
                .build();
        long start = System.currentTimeMillis();
        Page<TaleMetadata> taleMetadataPage = taleMetadataTable.index("GSI1")
                .query(request)
                .stream()
                .limit(1)
                .toList()
                .get(0);
        Set<String> uniqueCreatorIds = taleMetadataPage.items()
                .stream()
                .map(TaleMetadata::getCreatorId)
                .collect(Collectors.toSet());

        // Filter out tales of deactivated user
        List<User> uniqueUsers = userService.getUsersByUserIds(uniqueCreatorIds);
        List<TaleMetadata> filteredTaleMetadata = taleMetadataPage.items()
                .stream()
                .filter(taleMetadata -> {
                    Optional<User> user = uniqueUsers.stream()
                            .filter(u -> Objects.equals(u.getId(), taleMetadata.getCreatorId()))
                            .findFirst();
                    return user.isPresent() && !user.get().getIsDeactivated();
                })
                .toList();

        LOGGER.info("Time taken to get taleMetadataPage (ms): " + (System.currentTimeMillis() - start));
        return Page.create(filteredTaleMetadata, taleMetadataPage.lastEvaluatedKey());
    }

    public Page<TaleMetadata> getNextTalesMetadataPageByUserId(String userId) {
        QueryConditional conditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue(Tale.USER_PK_PREFIX + userId)
                .sortValue(Tale.USER_SK_PREFIX)
                .build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .build();
        Page<TaleMetadata> talesMetadataPage = taleMetadataTable.query(request)
                .stream()
                .limit(1)
                .toList()
                .get(0);

        return talesMetadataPage;
    }

    public Tale getTaleByTaleId(String taleId) {
        LOGGER.info("Getting tale: " + taleId);
        TaleMetadata taleMetadata = getTaleMetadataByTaleId(taleId);
        LOGGER.trace("Got taleMetadata: " + taleMetadata);

        // Get itinerary metadata
        GetItemEnhancedRequest itineraryMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX_ITINERARY)
                        .build())
                .build();
        BaseMetadata itineraryMetadata = baseMetadataTable.getItem(itineraryMetadataRequest);
        LOGGER.trace("Got itineraryMetadata: " + itineraryMetadata);

        // Get routes
        QueryConditional routesConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX_ROUTE)
                        .build()
        );
        QueryEnhancedRequest routesRequest = QueryEnhancedRequest.builder()
                .queryConditional(routesConditional)
//                .limit(5)
                .scanIndexForward(false)
                .build();
        List<Route> routes = routeTable.query(routesRequest)
                .items()
                .stream()
                .sorted()
                .toList();

        // Get story items
        QueryConditional storyItemsConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX_STORYITEM)
                        .build()
        );
        QueryEnhancedRequest storyItemsRequest = QueryEnhancedRequest.builder()
                .queryConditional(storyItemsConditional)
                .limit(5) // TODO: is this necessary?
                .scanIndexForward(true)
                .build();
        List<StoryItem> storyItems = storyItemTable.query(storyItemsRequest)
                .items()
                .stream()
                .sorted()
                .toList();

        return new Tale(taleMetadata, new Itinerary(itineraryMetadata, routes), storyItems);
    }

    public TaleMetadata getTaleMetadataByTaleId(String taleId) {
        GetItemEnhancedRequest taleMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX)
                        .build())
                .build();

        return taleMetadataTable.getItem(taleMetadataRequest);
    }

    public TaleMetadata getUserTaleMetadataByTaleId(String userId, String taleId) {
        GetItemEnhancedRequest taleMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(Tale.USER_PK_PREFIX)
                        .sortValue(Tale.USER_SK_PREFIX + taleId)
                        .build())
                .build();
        return taleMetadataTable.getItem(taleMetadataRequest);
    }

    public List<TaleMetadata> getAllTalesMetadataByUserId(String userId) {
        List<TaleMetadata> allTaleMetadataList = new ArrayList<>();
        QueryConditional conditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue(Tale.GSI1PK_PREFIX)
                .sortValue(Tale.GSI1SK_PREFIX)
                .build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .filterExpression(Expression.builder()
                        .expression("creator.id=:userId")
                        .expressionValues(ImmutableMap.of(":userId", AttributeValue.fromS(userId)))
                        .build())
                .consistentRead(false) // TODO: should be true since we want to get every tale metadata?
                .build();
        SdkIterable<Page<TaleMetadata>> metadataPageIterable = taleMetadataTable.index("GSI1").query(request);

        metadataPageIterable.forEach(page -> {
            allTaleMetadataList.addAll(page.items());
        });

        return allTaleMetadataList;
    }

    public List<TaleMetadata> getAllUserTalesMetadataByUserId(String userId) {
        List<TaleMetadata> allTaleMetadataList = new ArrayList<>();
        QueryConditional conditional = QueryConditional.sortBeginsWith(Key.builder()
                .partitionValue(Tale.USER_PK_PREFIX + userId)
                .sortValue(Tale.USER_SK_PREFIX)
                .build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .consistentRead(false) // TODO: should be true since fetching all?
                .build();
        SdkIterable<TaleMetadata> metadataPageIterable = taleMetadataTable.query(request).items();
        metadataPageIterable.forEach(allTaleMetadataList::add);

        return allTaleMetadataList;
    }

    public List<BaseMetadata> getAllItineraryMetadataByTaleIdList(List<String> taleIds) {
        List<BaseMetadata> allItineraryMetadataList = new ArrayList<>();
        taleIds.forEach(taleId -> {
            BaseMetadata itineraryMetadata = baseMetadataTable.getItem(Key.builder()
                            .partitionValue(Tale.PK_PREFIX + taleId)
                            .sortValue(Tale.SK_PREFIX_ITINERARY)
                    .build());
            allItineraryMetadataList.add(itineraryMetadata);
        });

        return allItineraryMetadataList;
    }

    public Tale saveTale(Tale tale) {
        LOGGER.info("Saving tale: " + tale.getMetadata().getId());
        // Save user tale metadata
        TaleMetadata taleMetadata = tale.getMetadata();
        TaleMetadata userTaleMetadata = new TaleMetadata(
                Tale.USER_PK_PREFIX + taleMetadata.getCreatorId(),
                Tale.USER_SK_PREFIX + taleMetadata.getId(),
                taleMetadata.getId(),
                taleMetadata.getCreatorId(),
                taleMetadata.getCover(),
                taleMetadata.getThumbnail(),
                taleMetadata.getTitle()
        );
        taleMetadataTable.putItem(userTaleMetadata);
        LOGGER.trace("----------- User tale metadata saved successfully -----------");

        // Save tale metadata
        taleMetadata.setPK(Tale.PK_PREFIX + taleMetadata.getId());
        taleMetadata.setSK(Tale.SK_PREFIX);
        taleMetadata.setGSI1PK(Tale.GSI1PK_PREFIX);
        taleMetadata.setGSI1SK(taleMetadata.getPK());
        taleMetadataTable.putItem(taleMetadata);
        LOGGER.trace("----------- Tale metadata saved successfully -----------");

        // Save itinerary metadata
        BaseMetadata itineraryMetadata = tale.getItinerary().getMetadata();
        if (itineraryMetadata != null) {
            itineraryMetadata.setPK(Tale.PK_PREFIX + taleMetadata.getId());
            itineraryMetadata.setSK(Tale.SK_PREFIX_ITINERARY);
            baseMetadataTable.putItem(itineraryMetadata);
            LOGGER.trace("----------- Itinerary metadata saved successfully -----------");
        }

        // Save routes
        List<Route> routes = tale.getItinerary().getRoutes();
        if (routes != null && !routes.isEmpty()) {
            routes.forEach(route -> {
                route.setPK(taleMetadata.getPK());
                route.setSK(Tale.SK_PREFIX_ROUTE + route.getId());
            });
            List<WriteBatch> routesBatches = routes.stream()
                    .map(item -> WriteBatch.builder(Route.class)
                            .mappedTableResource(routeTable)
                            .addPutItem(item)
                            .build())
                    .toList();
            BatchWriteItemEnhancedRequest batchSaveRoutesRequest = BatchWriteItemEnhancedRequest.builder()
                    .writeBatches(routesBatches)
                    .build();
            BatchWriteResult batchSaveRoutesResult = dynamoDbEnhancedClient.batchWriteItem(batchSaveRoutesRequest);
            LOGGER.trace("----------- Routes saved successfully -----------");
        }


        // Save story items
        List<StoryItem> storyItems = tale.getStory();
        if (storyItems != null && !storyItems.isEmpty()) {
            storyItems.forEach(storyItem -> {
                storyItem.setPK(taleMetadata.getPK());
                storyItem.setSK(Tale.SK_PREFIX_STORYITEM + storyItem.getId());
            });
            List<String> linkedFeedIds = storyItems.stream()
                    .filter(storyItem -> storyItem.getType() == 1)
                    .map(storyItem -> storyItem.getData().get("feedId"))
                    .toList();
            List<FeedMetadata> linkedFeedsMetadata = feedService.getFeedMetadataListByFeedIds(linkedFeedIds);
            linkedFeedsMetadata.forEach(metadata -> {
                metadata.setTaleId(taleMetadata.getId());
            });
            feedService.updateFeedsTaleId(linkedFeedsMetadata);

            List<WriteBatch> storyItemsBatches = storyItems
                    .stream()
                    .map(item -> WriteBatch.builder(StoryItem.class)
                            .mappedTableResource(storyItemTable)
                            .addPutItem(item)
                            .build())
                    .toList();
            BatchWriteItemEnhancedRequest batchSaveStoryItemsRequest = BatchWriteItemEnhancedRequest
                    .builder()
                    .writeBatches(storyItemsBatches)
                    .build();
            BatchWriteResult batchSaveStoryItemsResponse = dynamoDbEnhancedClient.batchWriteItem(batchSaveStoryItemsRequest);
            LOGGER.trace("----------- Story items saved successfully -----------");

            // TODO: Check for unprocessed items in batch write request
            // batchSaveStoryItemsResponse.unprocessedPutItemsForTable()
            // batchSaveStoryItemsResponse.unprocessedDeleteItemsForTable(storyItemTable)
        }

        return tale;
    }

    public TaleMetadata updateTaleMetadata(TaleMetadata taleMetadata) {
        UpdateItemEnhancedRequest<TaleMetadata> request = UpdateItemEnhancedRequest.builder(TaleMetadata.class)
                .item(taleMetadata)
                .ignoreNulls(true)
                .build();

        return taleMetadataTable.updateItem(request);
    }

    public TaleMetadata updateTaleMetadata(TaleMetadata taleMetadata, Boolean ignoreNulls) {
        UpdateItemEnhancedRequest<TaleMetadata> request = UpdateItemEnhancedRequest.builder(TaleMetadata.class)
                .item(taleMetadata)
                .ignoreNulls(ignoreNulls)
                .build();
        return taleMetadataTable.updateItem(request);
    }

    private BaseMetadata updateBaseMetadata(BaseMetadata metadata) {
        UpdateItemEnhancedRequest<BaseMetadata> request = UpdateItemEnhancedRequest.builder(BaseMetadata.class)
                .item(metadata)
                .ignoreNulls(true)
                .build();
        return baseMetadataTable.updateItem(request);
    }

    public Tale updateTale(UpdateTaleRequestBody body) {
        String taleId = body.taleId();
        List<TaleMetadata> modifiedMetadataList = body.metadata().getModified();
        List<String> deletedCoverIds = body.metadata().getDeleted();
        List<Route> modifiedRoutes = body.routes().getModified();
        List<String> deletedRouteIds = body.routes().getDeleted();
        List<StoryItem> modifiedStoryItems = body.storyItems().getModified();
        List<String> deletedStoryItemIds = body.storyItems().getDeleted();

        // Update metadata
        if (modifiedMetadataList != null && !modifiedMetadataList.isEmpty()) {
            TaleMetadata modifiedMetadata = modifiedMetadataList.get(0);
            TaleMetadata userTaleMetadata = getUserTaleMetadataByTaleId(modifiedMetadata.getCreatorId(), taleId);
            modifiedMetadata.setPK(Tale.PK_PREFIX + modifiedMetadata.getId());
            modifiedMetadata.setSK(Tale.SK_PREFIX);
            modifiedMetadata.setGSI1PK(Tale.GSI1PK_PREFIX);
            modifiedMetadata.setGSI1SK(Tale.GSI1SK_PREFIX + modifiedMetadata.getId());
            userTaleMetadata.setCover(modifiedMetadata.getCover());
            userTaleMetadata.setThumbnail(modifiedMetadata.getThumbnail());
            userTaleMetadata.setTitle(modifiedMetadata.getTitle());

            if (modifiedMetadata.getTitle().length() == 0) {
                // If empty string title received (not possible from frontend anyway), ignore change
                modifiedMetadata.setTitle(null);
                userTaleMetadata.setTitle(null);
            }

            if (deletedCoverIds != null && !deletedCoverIds.isEmpty()) {
                if (modifiedMetadata.getCover() == null) {
                    // TODO: delete old cover media, no new
                    TaleMetadata updatedMetadata = updateTaleMetadata(modifiedMetadata, false);
                    TaleMetadata updatedUserTaleMetadata = updateTaleMetadata(userTaleMetadata, false);
                } else {
                    // TODO: delete old cover media, added new
                    modifiedMetadata.setGSI1PK(null);
                    modifiedMetadata.setGSI1SK(null);
                    modifiedMetadata.setId(null);
                    modifiedMetadata.setCreatorId(null);
                    userTaleMetadata.setId(null);
                    userTaleMetadata.setCreatorId(null);
                    TaleMetadata updatedMetadata = updateTaleMetadata(modifiedMetadata);
                    TaleMetadata updatedUserTaleMetadata = updateTaleMetadata(userTaleMetadata);
                }

                // TODO: send deletedCoverId to MQ for deleting cover and thumbnail from bucket
            } else {
                modifiedMetadata.setGSI1PK(null);
                modifiedMetadata.setGSI1SK(null);
                modifiedMetadata.setId(null);
                modifiedMetadata.setCreatorId(null);
                userTaleMetadata.setId(null);
                userTaleMetadata.setCreatorId(null);
                TaleMetadata updatedMetadata = updateTaleMetadata(modifiedMetadata);
                TaleMetadata updatedUserTaleMetadata = updateTaleMetadata(userTaleMetadata);
            }
        }

        // Update routes
        if (modifiedRoutes != null && !modifiedRoutes.isEmpty()) {
            modifiedRoutes.forEach(route -> {
                route.setPK(Tale.PK_PREFIX);
                route.setSK(Tale.SK_PREFIX_ROUTE + route.getId());
            });
            List<Route> updatedRoutes = batchUpdateRoutes(modifiedRoutes);
        }

        if (deletedRouteIds != null && !deletedRouteIds.isEmpty()) {
            BatchWriteResult deleteRoutesResult = batchDeleteRoutes(taleId, deletedRouteIds);
        }

        // Update story items
        if (deletedStoryItemIds != null && !deletedStoryItemIds.isEmpty()) {
            // Check for feeds that have taleIds associated
            List<StoryItem> storyItemsToBeDeleted = batchGetStoryItems(taleId, deletedStoryItemIds);
            List<String> feedIdsToBeUnlinked = storyItemsToBeDeleted.stream()
                    .filter(storyItem -> storyItem.getType() == 1)
                    .map(storyItem -> storyItem.getData().get("feedId"))
                    .toList();
            List<FeedMetadata> metadataListToBeUnlinked = feedService.getFeedMetadataListByFeedIds(feedIdsToBeUnlinked);
            metadataListToBeUnlinked.forEach(feedMetadata -> {
                feedMetadata.setTaleId("");
            });
            List<FeedMetadata> unlinkedMetadataList = feedService.updateFeedsTaleId(metadataListToBeUnlinked);
            BatchWriteResult deleteStoryItemsResult = batchDeleteStoryItems(taleId, deletedStoryItemIds);
        }

        if (modifiedStoryItems != null && !modifiedStoryItems.isEmpty()) {
            modifiedStoryItems.forEach(storyItem -> {
                storyItem.setPK(Tale.PK_PREFIX + taleId);
                storyItem.setSK(Tale.SK_PREFIX_STORYITEM + storyItem.getId());
            });

            // Check if there are any new storymedia (feed) items added that need to update taleId
            List<StoryItem> updatedStoryItems = batchUpdateStoryItems(modifiedStoryItems);
            List<String> updatedFeedIds = updatedStoryItems.stream()
                    .filter(storyItem -> storyItem.getType() == 1)
                    .map(storyItem -> storyItem.getData().get("feedId"))
                    .toList();
            List<FeedMetadata> metadataList = feedService.getFeedMetadataListByFeedIds(updatedFeedIds);
            List<FeedMetadata> newlyLinkedFeedMetadataList = metadataList.stream()
                    .filter(feedMetadata -> Objects.equals(feedMetadata.getTaleId(), ""))
                    .toList();
            if (!newlyLinkedFeedMetadataList.isEmpty()) {
                newlyLinkedFeedMetadataList.forEach(feedMetadata -> feedMetadata.setTaleId(taleId));
                feedService.updateFeedsTaleId(newlyLinkedFeedMetadataList);
            }
        }

        return getTaleByTaleId(taleId);
    }

    public String deleteTaleByTaleId(String taleId) {
        LOGGER.info("Deleting tale by taleId: " + taleId);

        // Delete tale metadata
        DeleteItemEnhancedRequest deleteTaleMetadataRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(Tale.PK_PREFIX+ taleId)
                        .sortValue(Tale.SK_PREFIX)
                        .build())
                .build();
        DeleteItemEnhancedResponse<TaleMetadata> deleteTaleMetadataResponse = taleMetadataTable.deleteItemWithResponse(deleteTaleMetadataRequest);

        // Delete itinerary metadata
        DeleteItemEnhancedRequest deleteItineraryMetadataRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX_ITINERARY)
                        .build())
                .build();
        DeleteItemEnhancedResponse<BaseMetadata> itineraryMetadataResponse = baseMetadataTable.deleteItemWithResponse(deleteItineraryMetadataRequest);

        // Delete routes
        QueryConditional routesConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX_ROUTE)
                        .build()
        );
        QueryEnhancedRequest routesGetRequest = QueryEnhancedRequest.builder()
                .queryConditional(routesConditional)
                .scanIndexForward(false)
                .build();
        List<Route> routes = routeTable.query(routesGetRequest)
                .items()
                .stream()
                .toList();
        List<WriteBatch> routesBatches = routes.stream()
                .map(item -> WriteBatch.builder(Route.class)
                        .mappedTableResource(routeTable)
                        .addDeleteItem(item)
                        .build())
                .toList();
        BatchWriteItemEnhancedRequest batchDeleteRoutesRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(routesBatches)
                .build();
        BatchWriteResult batchDeleteRoutesResponse = dynamoDbEnhancedClient.batchWriteItem(batchDeleteRoutesRequest);

        // Delete story items
        QueryConditional storyItemsConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Tale.PK_PREFIX + taleId)
                        .sortValue(Tale.SK_PREFIX_STORYITEM)
                        .build()
        );
        QueryEnhancedRequest storyItemsGetRequest = QueryEnhancedRequest.builder()
                .queryConditional(storyItemsConditional)
                .scanIndexForward(false)
                .build();
        List<StoryItem> storyItems = storyItemTable.query(storyItemsGetRequest)
                .items()
                .stream()
                .toList();
        List<WriteBatch> storyItemsBatches = storyItems.stream()
                .map(item -> WriteBatch.builder(StoryItem.class)
                        .mappedTableResource(storyItemTable)
                        .addDeleteItem(item)
                        .build())
                .toList();
        BatchWriteItemEnhancedRequest batchDeleteStoryItemsRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(storyItemsBatches)
                .build();
        BatchWriteResult batchDeleteStoryItemsResponse = dynamoDbEnhancedClient.batchWriteItem(batchDeleteStoryItemsRequest);

        return taleId;
    }

}