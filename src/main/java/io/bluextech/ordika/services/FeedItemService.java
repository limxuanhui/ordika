package io.bluextech.ordika.services;
/* Created by limxuanhui on 14/1/24 */

import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.repositories.FeedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FeedItemService {

    @Autowired
    private final FeedItemRepository feedItemRepository;

    public List<FeedItem> findFeedItemsByFeedId(UUID id) {
        return feedItemRepository.findFeedItemsByFeedId(id);
    }

    public Boolean saveFeedItems(List<FeedItem> feedItems) {
        feedItemRepository.saveAll(feedItems);
        return true;
    }

}
