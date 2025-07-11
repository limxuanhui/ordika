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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tales")
public class TaleController {

    private final TaleService taleService;
    private final FeedService feedService;

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

        return new TaleViewBody(tale, feedList);
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
        return taleService.updateTale(body);
    }

    @DeleteMapping("/{taleId}")
    public String deleteTaleByTaleId(@PathVariable String taleId) {
        return taleService.deleteTaleByTaleId(taleId);
    }

}