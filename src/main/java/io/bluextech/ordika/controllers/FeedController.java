package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 8/1/24 */

import io.bluextech.ordika.dto.SaveFeedRequestBody;
import io.bluextech.ordika.exceptions.NoUserFoundException;
import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.services.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feeds")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping("/{id}")
    public Feed getFeedById(@PathVariable UUID id) {
        return feedService.getFeed(id);
    }

    @GetMapping(params = {"!page", "!size"})
    public List<Feed> getAllFeeds() {
        return feedService.getAllFeeds();
    }

    @GetMapping
    public Page<Feed> getFeedPage(@RequestParam(name = "page") int page,
                                  @RequestParam(name = "page") int size,
                                  Pageable pageable) {
        System.out.println("Getting page: " + page + " of size: " + size);

        return feedService.getFeedPage(pageable);
    }

    @PostMapping
    public Boolean saveFeed(@RequestBody SaveFeedRequestBody body) {
        System.out.println("FEED ITEMS: ");
        System.out.println(body);
        try {
            UUID newFeedId = feedService.saveFeed(body.getCreatorId(), body.getFeedItems());
            System.out.println("newfeedid: " + newFeedId);
        } catch (NoUserFoundException e) {

        }

        return true;
    }

}
