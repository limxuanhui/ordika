package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 8/1/24 */

import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.FeedMetadata;
import io.bluextech.ordika.services.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping("/{feedId}")
    public Feed getFeedByFeedId(@PathVariable String feedId) {
        return feedService.getFeedById(feedId);
    }

    @GetMapping("/metadata/{cursor}")
    public List<FeedMetadata> getNextFeedsMetadataPage(@PathVariable String cursor) {
        return feedService.getNextFeedsMetadataPage(cursor);
    }

    @GetMapping("/metadata/{userId}")
    public List<FeedMetadata> getNextFeedsMetadataPageByUserId(@PathVariable String userId) {
        return feedService.getNextFeedsMetadataPageByUserId(userId);
    }

    @PostMapping("/new")
    // Use Principal? to check user is logged in
    public Feed createFeed(@RequestBody Feed feed) {
        System.out.println(feed);
        return feedService.createFeed(feed);
    }

    @PutMapping("/edit")
    // Use Principal? to check user is logged in and is owner of Feed
    public Feed updateFeed(@RequestBody Feed feed) {
        return feedService.updateFeed(feed);
    }

    @DeleteMapping("/{feedId}/{userId}")
    // Use Principal? to check user is logged in and is owner of Feed
    public String deleteFeedByFeedId(@PathVariable String feedId, @PathVariable String userId) {
//    public String deleteFeedByFeedId(HttpServletRequest request) {
        return feedService.deleteFeedByFeedId(feedId);
//        System.out.println("changeSessionId: " + request.changeSessionId());
//        System.out.println("UserPrincipal: " + request.getUserPrincipal());
//        System.out.println("AuthType: " + request.getAuthType());
//        System.out.println("Method: " + request.getMethod());
//        System.out.println("ContextPath: " + request.getContextPath());
//        System.out.println("RemoteUser: " + request.getRemoteUser());
//        System.out.println("PathInfo: " + request.getPathInfo());
//        return "Testing servlet request";
    }

}