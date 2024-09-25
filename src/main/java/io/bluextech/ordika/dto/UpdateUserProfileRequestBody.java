package io.bluextech.ordika.dto;
/* Created by limxuanhui on 20/9/24 */

import io.bluextech.ordika.models.Media;

public record UpdateUserProfileRequestBody(String userId, Media avatar,
                                           String name, String handle, String bio) {}
