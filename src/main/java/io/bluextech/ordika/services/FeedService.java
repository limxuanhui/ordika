package io.bluextech.ordika.services;
/* Created by limxuanhui on 8/1/24 */

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bluextech.ordika.dto.UpdateFeedRequestBody;
import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.models.FeedMetadata;
import io.bluextech.ordika.models.PagedResult;
import io.bluextech.ordika.repositories.FeedRepository;
import io.bluextech.ordika.utils.DynamoDbAttributeValueConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

@Service
public class FeedService {

    @Autowired
    private FeedRepository feedRepository;

    public Feed getFeedById(String feedId) {
        return feedRepository.getFeedByFeedId(feedId);
    }

    public List<Feed> getFeedsListByFeedIds(List<String> feedIds) {
        return feedRepository.getFeedsListByFeedIds(feedIds);
    }

    public List<FeedMetadata> getFeedMetadataListByFeedIds(List<String> feedIds) {
        return feedRepository.getFeedMetadataListByFeedIds(feedIds);
    }

    public PagedResult<Feed> getNextFeedsPage(String base64Key) throws JsonProcessingException {
        Map<String, AttributeValue> exclusiveStartKey = null;
        if (base64Key != null) {
            exclusiveStartKey = DynamoDbAttributeValueConverter.decodeKeyFromBase64String(base64Key);
        }
        Page<Feed> feedPage = feedRepository.getNextFeedsPage(exclusiveStartKey);
        String lastEvaluatedKey = null;
        if (feedPage.lastEvaluatedKey() != null) {
            lastEvaluatedKey = DynamoDbAttributeValueConverter.encodeKeyToBase64String(feedPage.lastEvaluatedKey());
        }
        return new PagedResult<>(feedPage.items(), lastEvaluatedKey);
    }

    public PagedResult<Feed> getNextFeedsPageByUserId(String userId) throws JsonProcessingException {
        Page<Feed> feedPage = feedRepository.getNextFeedsPageByUserId(userId);
        String lastEvaluatedKey = null;
        if (feedPage.lastEvaluatedKey() != null) {
            lastEvaluatedKey = DynamoDbAttributeValueConverter.encodeKeyToBase64String(feedPage.lastEvaluatedKey());
        }
        return new PagedResult<>(feedPage.items(), lastEvaluatedKey);
    }

    public List<Feed> getAllFeedsByUserId(String userId) {
        return feedRepository.getAllFeedsByUserId(userId);
    }

    public PagedResult<FeedMetadata> getNextFeedsMetadataPageByUserId(String userId) throws JsonProcessingException {
        Page<FeedMetadata> feedMetadataPage = feedRepository.getNextFeedsMetadataPageByUserId(userId);
        String lastEvaluatedKey = null;
        if (feedMetadataPage.lastEvaluatedKey() != null) {
            lastEvaluatedKey = DynamoDbAttributeValueConverter.encodeKeyToBase64String(feedMetadataPage.lastEvaluatedKey());
        }
        return new PagedResult<>(feedMetadataPage.items(), lastEvaluatedKey);
    }

    public List<FeedMetadata> getAllFeedsMetadataByUserId(String userId) {
        return feedRepository.getAllFeedsMetadataByUserId(userId);
    }

    public List<FeedMetadata> getAllUserFeedsMetadataByUserId(String userId) {
        return feedRepository.getAllUserFeedsMetadataByUserId(userId);
    }

    public List<FeedMetadata> activateAllFeedsByUserId(String userId) {
        List<FeedMetadata> allFeedsMetadata = getAllFeedsMetadataByUserId(userId);
        List<FeedMetadata> allUserFeedsMetadata = getAllUserFeedsMetadataByUserId(userId);
        allFeedsMetadata.addAll(allUserFeedsMetadata);
        allFeedsMetadata.forEach(feedMetadata -> {
            feedMetadata.getCreator().setIsDeactivated(false);
            feedMetadata.setId(null);
            feedMetadata.setThumbnail(null);
            feedMetadata.setTaleId(null);
            feedMetadata.setGSI1PK(null);
            feedMetadata.setGSI1SK(null);
        });

        return feedRepository.batchUpdateFeedMetadata(allFeedsMetadata);
    }

    public List<FeedMetadata> deactivateAllFeedsByUserId(String userId) {
        List<FeedMetadata> allFeedsMetadata = getAllFeedsMetadataByUserId(userId);
        List<FeedMetadata> allUserFeedsMetadata = getAllUserFeedsMetadataByUserId(userId);
        allFeedsMetadata.addAll(allUserFeedsMetadata);
        allFeedsMetadata.forEach(feedMetadata -> {
            feedMetadata.getCreator().setIsDeactivated(true);
            feedMetadata.setId(null);
            feedMetadata.setThumbnail(null);
            feedMetadata.setTaleId(null);
            feedMetadata.setGSI1PK(null);
            feedMetadata.setGSI1SK(null);
        });

        return feedRepository.batchUpdateFeedMetadata(allFeedsMetadata);
    }

    public Feed createFeed(Feed feed) {
        return feedRepository.createNewFeed(feed);
    }

    public UpdateFeedRequestBody updateFeed(UpdateFeedRequestBody body) {
        FeedMetadata feedMetadata = body.getMetadata();
        System.out.println("FEED metadata: " + feedMetadata);
        if (feedMetadata != null) {
            feedMetadata.setPK("FEED#" + feedMetadata.getId());
            feedMetadata.setSK("#METADATA");
            feedMetadata.setGSI1PK("METADATA_FEED");
            feedMetadata.setGSI1SK("FEED#" + feedMetadata.getId());

            FeedMetadata userFeedMetadata = new FeedMetadata(
                    feedMetadata.getCreator().getPK(),
                    "#METADATA_FEED#FEED#" + feedMetadata.getId(),
                    feedMetadata.getId(),
                    feedMetadata.getCreator(),
                    feedMetadata.getThumbnail(),
                    feedMetadata.getTaleId());

            feedRepository.saveFeedMetadata(feedMetadata);
            feedRepository.saveFeedMetadata(userFeedMetadata);
        }

        List<FeedItem> modified = body.getModified();
        if (modified != null && !modified.isEmpty()) {
            modified.forEach(feedItem -> {
                feedItem.setPK("FEED#" + feedItem.getFeedId());
                feedItem.setSK("FEED_ITEM#" + feedItem.getId());
            });
            feedRepository.batchUpdateFeedItems(modified);
        }

        List<FeedItem> deleted = body.getDeleted();
        if (deleted != null && !deleted.isEmpty()) {
            deleted.forEach(feedItem -> {
                feedItem.setPK("FEED#" + feedItem.getFeedId());
                feedItem.setSK("FEED_ITEM#" + feedItem.getId());
            });
            feedRepository.batchDeleteFeedItems(deleted);
        }

        return body;
    }

    public List<FeedMetadata> updateFeedsTaleId(List<FeedMetadata> feedsMetadata) {
        System.out.println("Updating feeds tale id...");
        return feedRepository.updateFeedsTaleId(feedsMetadata);
    }

    public Feed deleteFeed(Feed feed) {
        return feedRepository.deleteFeed(feed);
    }

//    public List<Feed> deleteAllFeedsByUserId()

}