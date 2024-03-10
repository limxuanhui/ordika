package io.bluextech.ordika.dto;
/* Created by limxuanhui on 14/1/24 */

import io.bluextech.ordika.models.FeedItem;
import lombok.Data;

import java.util.List;

@Data
public class SaveFeedRequestBody {

    private String creatorId;
    private List<FeedItem> feedItems;

}
