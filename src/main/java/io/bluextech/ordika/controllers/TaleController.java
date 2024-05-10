package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 24/12/23 */

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bluextech.ordika.dto.TaleViewBody;
import io.bluextech.ordika.dto.UpdateTaleRequestBody;
import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.PagedResult;
import io.bluextech.ordika.models.Tale;
import io.bluextech.ordika.models.TaleMetadata;
import io.bluextech.ordika.services.FeedService;
import io.bluextech.ordika.services.TaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tales")
public class TaleController {

    @Autowired
    private TaleService taleService;

    @Autowired
    private FeedService feedService;

    @GetMapping("/metadata")
    public PagedResult<TaleMetadata> getNextTalesMetadataPage(@RequestParam(required = false) String base64Key) throws JsonProcessingException {
        return taleService.getNextTalesMetadataPage(base64Key);
    }

    @GetMapping("/{taleId}")
    public TaleViewBody getTaleByTaleId(@PathVariable String taleId) {
        Tale tale = taleService.getTaleByTaleId(taleId);
        List<String> feedIds = new ArrayList<>();
        tale.getStory().forEach(storyItem -> {
            if (storyItem.getData().get("feedId") != null) {
                feedIds.add(storyItem.getData().get("feedId"));
            }
        });
        List<Feed> feedList = feedService.getFeedsListByFeedIds(feedIds);
        TaleViewBody c = new TaleViewBody(tale, feedList);
        System.out.println("Get tale by tale id");
        System.out.println(c);
        return c;
    }

    @GetMapping("/metadata/user/{userId}")
    public PagedResult<TaleMetadata> getNextTalesMetadataPageByUserId(@PathVariable String userId) throws JsonProcessingException {
        return taleService.getNextTalesMetadataPageByUserId(userId);
    }

    @PostMapping("/new")
    public Tale createTale(@RequestBody Tale tale) {
        return taleService.createTale(tale);
    }

    @PutMapping("/edit")
    public Tale updateTale(@RequestBody UpdateTaleRequestBody body) {
        System.out.println("TALEID: " + body.taleId());
        System.out.println("METADATA:" + body.metadata());
        System.out.println("ROUTES: " + body.routes());
        System.out.println("STORY ITEMS: " + body.storyItems());
        return taleService.updateTale(body);
    }

    @DeleteMapping("/{taleId}")
    public String deleteTaleByTaleId(@PathVariable String taleId) {
        return taleService.deleteTaleByTaleId(taleId);
    }

}