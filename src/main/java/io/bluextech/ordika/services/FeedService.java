package io.bluextech.ordika.services;
/* Created by limxuanhui on 8/1/24 */

import io.bluextech.ordika.exceptions.NoUserFoundException;
import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.repositories.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FeedService {

    @Autowired
    private final FeedRepository feedRepository;

    @Autowired
    private final FeedItemService feedItemService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final MediaService mediaService;

    private List<FeedItem> getFeedItems(UUID feedId) {
        return feedItemService.findFeedItemsByFeedId(feedId);
    }

    public Feed getFeed(UUID id) {
        Feed feed = feedRepository.findById(id).orElse(null);

        if (feed != null) {
            List<FeedItem> feedItems = getFeedItems(id);
            feed.setItems(feedItems);
        }

        return feed;
    }

    public UUID saveFeed(String creatorId, List<FeedItem> feedItems) {
        User creator = userService.fetchUser(creatorId);
        if (creator == null) {
            throw new NoUserFoundException("No users found with id: " + creatorId);
        }
        Feed newFeed = new Feed(creator);
        newFeed.setCreator(creator);

        Feed savedFeed = feedRepository.save(newFeed);
        for (FeedItem feedItem : feedItems) {
            feedItem.setFeedId(savedFeed.getId());
            mediaService.saveMedia(feedItem.getMedia());
        }
        feedItemService.saveFeedItems(feedItems);

        return savedFeed.getId();
    }

    public List<Feed> getAllFeeds() {
        System.out.println("Getting all feeds!");
        List<Feed> feeds = feedRepository.findAll();
        for (Feed feed : feeds) {
            List<FeedItem> feedItems = getFeedItems(feed.getId());
            feed.setItems(feedItems);
        }

        return feeds;
    }

    public Page<Feed> getFeedPage(Pageable pageable) {
        Page<Feed> feedPage = feedRepository.findAll(pageable);
        for (Feed feed : feedPage) {
            List<FeedItem> feedItems = getFeedItems(feed.getId());
            feed.setItems(feedItems);
        }

        return feedPage;
    }

}
