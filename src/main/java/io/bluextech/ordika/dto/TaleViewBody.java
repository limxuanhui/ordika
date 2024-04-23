package io.bluextech.ordika.dto;
/* Created by limxuanhui on 30/3/24 */

import io.bluextech.ordika.models.Feed;
import io.bluextech.ordika.models.Tale;

import java.util.List;

public record TaleViewBody(Tale tale, List<Feed> feedList) {}
