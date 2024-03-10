package io.bluextech.ordika.repositories;

import io.bluextech.ordika.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.List;

@Repository
public class TaleRepository {

    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Autowired
    private DynamoDbTable<TaleMetadata> taleMetadataTable;
    @Autowired
    private DynamoDbTable<BaseMetadata> baseMetadataTable;
    @Autowired
    private DynamoDbTable<Route> routeTable;
    @Autowired
    private DynamoDbTable<StoryItem> storyItemTable;

    // TalesOverviewScreen, tales metadata
    public List<TaleMetadata> getNextTalesMetadataPage(String cursor) {
        // Check if there are anymore metadata pages to fetch,
        // if not log "No more new tales metadata"
        QueryConditional conditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("METADATA_TALE")
                        .sortValue("TALE#")
                        .build()
        );
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(conditional)
                .limit(10)
                .scanIndexForward(false)
                .build();
        SdkIterable<Page<TaleMetadata>> taleMetadataPages = taleMetadataTable.index("GSI1")
                .query(request);
        System.out.println("getting tale metadata...");
        taleMetadataPages.forEach(page -> {
            List<TaleMetadata> taleList = page.items();
            System.out.println(taleList);
        });

        return null;
    }

    // ProfileScreen, MyTales
    public List<TaleMetadata> getNextTalesMetadataPageByUserId(String userId) {
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
        List<TaleMetadata> talesMetadata = taleMetadataTable.query(request).items().stream().toList();
        System.out.println(talesMetadata);
        return talesMetadata;
    }

    // TaleViewScreen -> WriteTaleScreen (for new/edit)
    public Tale getTaleByTaleId(String taleId) {
        // Get tale metadata
        GetItemEnhancedRequest taleMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder().partitionValue("TALE#" + taleId).sortValue("#METADATA").build())
                .build();
        TaleMetadata taleMetadata = taleMetadataTable.getItem(taleMetadataRequest);
        System.out.println("Got taleMetadata: " + taleMetadata);

        // Get itinerary metadata
        GetItemEnhancedRequest itineraryMetadataRequest = GetItemEnhancedRequest.builder()
                .key(Key.builder().partitionValue("TALE#" + taleId).sortValue("#METADATA_ITINERARY").build())
                .build();
        BaseMetadata itineraryMetadata = baseMetadataTable.getItem(itineraryMetadataRequest);

        // Get routes
        QueryConditional routesConditional = QueryConditional.sortBeginsWith(
                Key.builder()
                        .partitionValue("TALE#" + taleId)
                        .sortValue("ITINERARY#ROUTE#")
                        .build()
        );
        QueryEnhancedRequest routesRequest = QueryEnhancedRequest.builder()
                .queryConditional(routesConditional)
                .limit(5)
                .scanIndexForward(false)
                .build();
        List<Route> routes = routeTable.query(routesRequest).items().stream().sorted().toList();

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
        List<StoryItem> storyItems = storyItemTable.query(storyItemsRequest).items().stream().sorted().toList();

        Tale tale = new Tale(taleMetadata, new Itinerary(itineraryMetadata, routes), storyItems);
        System.out.println(tale);
        return tale;
    }

    public Tale saveTale(Tale tale) {
        // Save tale metadata
        PutItemEnhancedRequest<TaleMetadata> taleMetadataRequest = PutItemEnhancedRequest.builder(TaleMetadata.class)
                .item(tale.getMetadata())
                .build();
        PutItemEnhancedResponse<TaleMetadata> taleMetadataResponse = taleMetadataTable.putItemWithResponse(taleMetadataRequest);
        System.out.println("Tale metadata SAVE response");
        System.out.println("Attributes: " + taleMetadataResponse.attributes());
        System.out.println("Consumed capacity: " + taleMetadataResponse.consumedCapacity());
        System.out.println("Item collection metrics: " + taleMetadataResponse.itemCollectionMetrics());

        // Save itinerary metadata
        PutItemEnhancedRequest<BaseMetadata> itineraryMetadataRequest = PutItemEnhancedRequest.builder(BaseMetadata.class)
                .item(tale.getItinerary().getMetadata())
                .build();
        PutItemEnhancedResponse<BaseMetadata> itineraryMetadataResponse = baseMetadataTable.putItemWithResponse(itineraryMetadataRequest);
        System.out.println("Itinerary metadata SAVE response");
        System.out.println("Attributes: " + itineraryMetadataResponse.attributes());
        System.out.println("Consumed capacity: " + itineraryMetadataResponse.consumedCapacity());
        System.out.println("Item collection metrics: " + itineraryMetadataResponse.itemCollectionMetrics());

        // Save routes
        List<WriteBatch> routesBatches = tale.getItinerary()
                .getRoutes()
                .stream()
                .map(item -> WriteBatch.builder(Route.class)
                        .mappedTableResource(routeTable)
                        .addPutItem(item)
                        .build())
                .toList();
        BatchWriteItemEnhancedRequest batchSaveRoutesRequest = BatchWriteItemEnhancedRequest
                .builder()
                .writeBatches(routesBatches)
                .build();
        BatchWriteResult batchSaveRoutesResult = dynamoDbEnhancedClient.batchWriteItem(batchSaveRoutesRequest);
        System.out.println("Routes SAVE");
        System.out.println(batchSaveRoutesResult.unprocessedDeleteItemsForTable(routeTable));

        // Save story items
        List<WriteBatch> storyItemsBatches = tale.getStory()
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
        System.out.println("Story items SAVE");
        System.out.println(batchSaveStoryItemsResponse.unprocessedDeleteItemsForTable(storyItemTable));

        return tale;
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