package io.bluextech.ordika.services;
/* Created by limxuanhui on 24/12/23 */

import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.models.Tale;
import io.bluextech.ordika.repositories.FeedItemRepository;
import io.bluextech.ordika.repositories.FeedRepository;
import io.bluextech.ordika.repositories.TaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaleService {

    @Autowired
    private TaleRepository taleRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private FeedItemRepository feedItemRepository;

    public List<Tale> getAllTales() {
        return taleRepository.findAll();
    }

    public List<Tale> getTalesMetadataPage() {
        return taleRepository.findAll();
    }

    public Tale getTale(UUID id) {
        Tale tale = taleRepository.findById(id).orElse(null);

        if (tale != null) {
            List<Feed> feeds = feedRepository.findFeedsByTaleId(id);
            for (Feed feed : feeds) {
                List<FeedItem> feedItems = feedItemRepository.findFeedItemsByFeedId(feed.getId());
                feed.setItems(feedItems);
            }
            tale.setFeeds(feeds);
        }

        return tale;
    }

    public void createTale() {}

    public void editTale() {}

    public void deleteTale(String id) {}

}
