package io.bluextech.ordika.services;
/* Created by limxuanhui on 8/1/24 */

import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedMetadata;
import io.bluextech.ordika.repositories.FeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    @Autowired
    private FeedRepository feedRepository;

    public Feed getFeedById(String feedId) {
        return feedRepository.getFeedByFeedId(feedId);
    }

    public List<FeedMetadata> getNextFeedsMetadataPage(String cursor) {
        return null;
    }

    public List<FeedMetadata> getNextFeedsMetadataPageByUserId(String userId) {
        return null;
    }

    public Feed createFeed(Feed feed) {
//        System.out.println(feed);
        return feedRepository.createNewFeed(feed);
    }

    public Feed updateFeed(Feed feed) {
        return null;
    }

    public String deleteFeedByFeedId(String feedId) {
        return feedRepository.deleteFeedByFeedId(feedId);
    }

}