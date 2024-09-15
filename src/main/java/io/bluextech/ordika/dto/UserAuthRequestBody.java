package io.bluextech.ordika.dto;
/* Created by limxuanhui on 7/1/24 */

import io.bluextech.ordika.models.User;

public record UserAuthRequestBody(User user, String idToken) {}
