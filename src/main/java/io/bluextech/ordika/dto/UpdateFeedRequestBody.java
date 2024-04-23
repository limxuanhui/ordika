package io.bluextech.ordika.dto;
/* Created by limxuanhui on 10/4/24 */

import io.bluextech.ordika.models.FeedItem;
import io.bluextech.ordika.models.FeedMetadata;
import lombok.Data;

import java.util.List;

@Data
public class UpdateFeedRequestBody {

    private FeedMetadata metadata;
    private List<FeedItem> modified;
    private List<FeedItem> deleted;

}
