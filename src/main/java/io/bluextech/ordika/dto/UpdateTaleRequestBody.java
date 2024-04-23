package io.bluextech.ordika.dto;
/* Created by limxuanhui on 16/4/24 */

import io.bluextech.ordika.models.Delta;
import io.bluextech.ordika.models.Route;
import io.bluextech.ordika.models.StoryItem;
import io.bluextech.ordika.models.TaleMetadata;

public record UpdateTaleRequestBody(String taleId,
                                    TaleMetadata metadata,
                                    Delta<Route> routes,
                                    Delta<StoryItem> storyItems) {}
