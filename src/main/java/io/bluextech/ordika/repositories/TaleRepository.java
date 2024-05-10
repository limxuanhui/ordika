package io.bluextech.ordika.repositories;

import io.bluextech.ordika.dto.UpdateTaleRequestBody;
import io.bluextech.ordika.models.*;
import io.bluextech.ordika.services.FeedService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class TaleRepository {

    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
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
            BaseMetadata updatedItineraryMetadata = updateItineraryMetadata(itineraryMetadata);
            updatedItineraryMetadataList.add(updatedItineraryMetadata);
        });

        return updatedItineraryMetadataList;
    }

    public BatchWriteResult batchSaveRoutes(List<Route> routes) {
        List<WriteBatch> saveBatches = routes
                .stream()
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
        System.out.println("Batch updating routes");
        routes.forEach(route -> {
            Route updatedRoute = routeTable.updateItem(route);
            System.out.println("Updated this route : " + updatedRoute);
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
        System.out.println("Batch updating story items");
        storyItems.forEach(storyItem -> {
            StoryItem updatedStoryItem = storyItemTable.updateItem(storyItem);
            System.out.println("Updated this story item : " + updatedStoryItem);
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
        System.out.println("Time taken to batch delete story items (ms): " + (System.currentTimeMillis() - start));
        return batchDeleteResult;
    }

    public Page<TaleMetadata> getNextTalesMetadataPage(Map<String, AttributeValue> exclusiveStartKey) {
        // Check if there are anymore metadata pages to fetch,
        // if not log "No more new tales metadata"
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue(Tale.GSI1PK_PREFIX)
                        .sortValue(Tale.GSI1SK_PREFIX)
                        .build()
        );
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .consistentRead(false)
                .exclusiveStartKey(exclusiveStartKey)
                .filterExpression(Expression.builder()
                        .expression("creator.isDeactivated=:isDeactivated")
                        .expressionValues(Map.of(":isDeactivated", AttributeValue.fromBool(false)))
                        .build())
                .build();
        long start = System.currentTimeMillis();
        Page<TaleMetadata> taleMetadataPage = taleMetadataTable.index("GSI1")
                .query(request)
                .stream()
                .limit(1)
                .toList()
                .get(0);
        System.out.println("getting tale metadata...");
//        taleMetadataPages.forEach(page -> {
//            List<TaleMetadata> taleList = page.items();
//            System.out.println(taleList);
//        });
        System.out.println("Time taken to get taleMetadataPage (ms): " + (System.currentTimeMillis() - start));
        return taleMetadataPage;
    }

    public Page<TaleMetadata> getNextTalesMetadataPageByUserId(String userId) {
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
        Page<TaleMetadata> talesMetadataPage = taleMetadataTable.query(request)
                .stream()
                .limit(1)
                .toList()
                .get(0);

        return talesMetadataPage;
    }

    public Tale getTaleByTaleId(String taleId) {
        // Get tale metadata
        TaleMetadata taleMetadata = getTaleMetadataByTaleId(taleId);
        System.out.println("Got taleMetadata: " + taleMetadata);

        // Get itinerary metadata
        GetItemEnhancedRequest itineraryMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("#METADATA_ITINERARY")
                        .build())
                .build();
        BaseMetadata itineraryMetadata = baseMetadataTable.getItem(itineraryMetadataRequest);
        System.out.println("Got itineraryMetadata: " + itineraryMetadata);

        // Get routes
        QueryConditional routesConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("ITINERARY#ROUTE#")
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
                        .partitionValue("TALE#" + taleId)
                        .sortValue("STORY#")
                        .build()
        );
        QueryEnhancedRequest storyItemsRequest = QueryEnhancedRequest.builder()
                .queryConditional(storyItemsConditional)
                .limit(5)
                .scanIndexForward(true)
                .build();
        List<StoryItem> storyItems = storyItemTable.query(storyItemsRequest)
                .items()
                .stream()
                .sorted()
                .toList();

        Tale tale = new Tale(taleMetadata, new Itinerary(itineraryMetadata, routes), storyItems);
        System.out.println(tale);
        return tale;
    }

    public TaleMetadata getTaleMetadataByTaleId(String taleId) {
        GetItemEnhancedRequest taleMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("#METADATA")
                        .build())
                .build();
        return taleMetadataTable.getItem(taleMetadataRequest);
    }

    public TaleMetadata getUserTaleMetadataByTaleId(String userId, String taleId) {
        GetItemEnhancedRequest taleMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue("USER#" + userId)
                        .sortValue("#METADATA_TALE#TALE#" + taleId)
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
//                        .expressionNames(ImmutableMap.of("#creatorId", "creator.id")) // not working
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
                            .sortValue(Tale.ITINERARY_SK_PREFIX)
                    .build());
            allItineraryMetadataList.add(itineraryMetadata);
        });

        return allItineraryMetadataList;
    }

    public Tale saveTale(Tale tale) {
        // Save user tale metadata
        TaleMetadata taleMetadata = tale.getMetadata();
        TaleMetadata userTaleMetadata = new TaleMetadata(
                "USER#" + taleMetadata.getCreator().getId(),
                "#METADATA_TALE#TALE#" + taleMetadata.getId(),
                taleMetadata.getId(),
                taleMetadata.getCreator(),
                taleMetadata.getCover(),
                taleMetadata.getThumbnail(),
                taleMetadata.getTitle()
        );
        System.out.println("User tale metadata to save: ");
        System.out.println(userTaleMetadata);
        taleMetadataTable.putItem(userTaleMetadata);

        // Save tale metadata
        taleMetadata.setPK("TALE#" + taleMetadata.getId());
        taleMetadata.setSK("#METADATA");
        taleMetadata.setGSI1PK("METADATA_TALE");
        taleMetadata.setGSI1SK(taleMetadata.getPK());
        taleMetadataTable.putItem(taleMetadata);
        System.out.println("----------- Tale metadata saved successfully -----------");

        // Save itinerary metadata
        BaseMetadata itineraryMetadata = tale.getItinerary().getMetadata();
        if (itineraryMetadata != null) {
            itineraryMetadata.setPK("TALE#" + taleMetadata.getId());
            itineraryMetadata.setSK("#METADATA_ITINERARY");
            baseMetadataTable.putItem(itineraryMetadata);
            System.out.println("----------- Itinerary metadata saved successfully -----------");
        }

        // Save routes
        List<Route> routes = tale.getItinerary().getRoutes();
        if (routes != null && !routes.isEmpty()) {
            routes.forEach(route -> {
                route.setPK(taleMetadata.getPK());
                route.setSK("ITINERARY#ROUTE#" + route.getId());
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
            System.out.println("----------- Routes saved successfully -----------");
            System.out.println(batchSaveRoutesResult.unprocessedDeleteItemsForTable(routeTable));
        }


        // Save story items
        List<StoryItem> storyItems = tale.getStory();
        if (storyItems != null && !storyItems.isEmpty()) {
            storyItems.forEach(storyItem -> {
                storyItem.setPK(taleMetadata.getPK());
                storyItem.setSK("STORY#STORY_ITEM#" + storyItem.getId());
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
            System.out.println("----------- Story items saved successfully -----------");
            System.out.println(batchSaveStoryItemsResponse.unprocessedDeleteItemsForTable(storyItemTable));
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

    private BaseMetadata updateItineraryMetadata(BaseMetadata itineraryMetadata) {
        UpdateItemEnhancedRequest<BaseMetadata> request = UpdateItemEnhancedRequest.builder(BaseMetadata.class)
                .item(itineraryMetadata)
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
            TaleMetadata userTaleMetadata = getUserTaleMetadataByTaleId(modifiedMetadata.getCreator().getId(), taleId);
            modifiedMetadata.setPK("TALE#" + modifiedMetadata.getId());
            modifiedMetadata.setSK("#METADATA");
            modifiedMetadata.setGSI1PK("METADATA_TALE");
            modifiedMetadata.setGSI1SK("TALE#" + modifiedMetadata.getId());
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
                    // deleted old, no new
                    TaleMetadata updatedMetadata = updateTaleMetadata(modifiedMetadata, false);
                    TaleMetadata updatedUserTaleMetadata = updateTaleMetadata(userTaleMetadata, false);
                } else {
                    // deleted old, added new
                    modifiedMetadata.setGSI1PK(null);
                    modifiedMetadata.setGSI1SK(null);
                    modifiedMetadata.setId(null);
                    modifiedMetadata.setCreator(null);
                    userTaleMetadata.setId(null);
                    userTaleMetadata.setCreator(null);
                    TaleMetadata updatedMetadata = updateTaleMetadata(modifiedMetadata);
                    TaleMetadata updatedUserTaleMetadata = updateTaleMetadata(userTaleMetadata);
                }

                // TODO: send deletedCoverId to MQ for deleting cover and thumbnail from bucket
            } else {
                modifiedMetadata.setGSI1PK(null);
                modifiedMetadata.setGSI1SK(null);
                modifiedMetadata.setId(null);
                modifiedMetadata.setCreator(null);
                userTaleMetadata.setId(null);
                userTaleMetadata.setCreator(null);
                TaleMetadata updatedMetadata = updateTaleMetadata(modifiedMetadata);
                TaleMetadata updatedUserTaleMetadata = updateTaleMetadata(userTaleMetadata);
            }
        }

        // Update routes
        if (modifiedRoutes != null && !modifiedRoutes.isEmpty()) {
            modifiedRoutes.forEach(route -> {
                route.setPK("TALE#" + taleId);
                route.setSK("ITINERARY#ROUTE#" + route.getId());
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
                storyItem.setPK("TALE#" + taleId);
                storyItem.setSK("STORY#STORY_ITEM#" + storyItem.getId());
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
        // Delete tale metadata
        DeleteItemEnhancedRequest deleteTaleMetadataRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("#METADATA")
                        .build())
                .build();
        DeleteItemEnhancedResponse<TaleMetadata> deleteTaleMetadataResponse = taleMetadataTable.deleteItemWithResponse(deleteTaleMetadataRequest);
        System.out.println("Tale metadata DELETE response");
        System.out.println("Attributes: " + deleteTaleMetadataResponse.attributes());
        System.out.println("Consumed capacity: " + deleteTaleMetadataResponse.consumedCapacity());
        System.out.println("Item collection metrics: " + deleteTaleMetadataResponse.itemCollectionMetrics());

        // Delete itinerary metadata
        DeleteItemEnhancedRequest deleteItineraryMetadataRequest = DeleteItemEnhancedRequest.builder()
                .key(Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("#METADATA_ITINERARY")
                        .build())
                .build();
        DeleteItemEnhancedResponse<BaseMetadata> itineraryMetadataResponse = baseMetadataTable.deleteItemWithResponse(deleteItineraryMetadataRequest);
        System.out.println("Itinerary metadata DELETE response");
        System.out.println("Attributes: " + itineraryMetadataResponse.attributes());
        System.out.println("Consumed capacity: " + itineraryMetadataResponse.consumedCapacity());
        System.out.println("Item collection metrics: " + itineraryMetadataResponse.itemCollectionMetrics());

        // Delete routes
        QueryConditional routesConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("ITINERARY#ROUTE#")
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
        System.out.println("Routes not yet DELETED");
        System.out.println(batchDeleteRoutesResponse.unprocessedDeleteItemsForTable(routeTable));

        // Delete story items
        QueryConditional storyItemsConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("STORY#")
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
        System.out.println("Story items not yet DELETED");
        System.out.println(batchDeleteStoryItemsResponse.unprocessedDeleteItemsForTable(storyItemTable));

        return taleId;
    }

}