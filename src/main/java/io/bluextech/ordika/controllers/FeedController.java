package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 8/1/24 */

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bluextech.ordika.dto.UpdateFeedRequestBody;
import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedMetadata;
import io.bluextech.ordika.models.PagedResult;
import io.bluextech.ordika.services.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/feeds")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public PagedResult<Feed> getNextFeedsPage(@RequestParam(required = false) String base64Key) throws JsonProcessingException {
        return feedService.getNextFeedsPage(base64Key);
    }

    @GetMapping("/{feedId}")
    public Feed getFeedByFeedId(@PathVariable String feedId) {
        return feedService.getFeedById(feedId);
    }

    @PostMapping("/list")
    public List<Feed> getFeedsListByFeedIds(@RequestBody List<String> feedIds) {
        return feedService.getFeedsListByFeedIds(feedIds);
    }

    @GetMapping("/user/{userId}")
    public PagedResult<Feed> getNextFeedsPageByUserId(@PathVariable String userId) throws JsonProcessingException {
        return feedService.getNextFeedsPageByUserId(userId);
    }

    @GetMapping("/user/all/{userId}")
    public List<Feed> getAllFeedsByUserId(@PathVariable String userId) {
        return feedService.getAllFeedsByUserId(userId);
    }

    @GetMapping("/metadata/user/{userId}")
    public PagedResult<FeedMetadata> getNextFeedsMetadataPageByUserId(@PathVariable String userId) throws JsonProcessingException {
        return feedService.getNextFeedsMetadataPageByUserId(userId);
    }

    @PostMapping("/new")
    public Feed createFeed(@RequestBody Feed feed) {
        return feedService.createFeed(feed);
    }

    @PutMapping("/edit")
    public UpdateFeedRequestBody updateFeed(@RequestBody UpdateFeedRequestBody body) {
        return feedService.updateFeed(body);
    }

    @DeleteMapping("/{feedId}/{userId}")
    public Feed deleteFeed(@RequestBody Feed feed) {
        return feedService.deleteFeed(feed);
    }

}