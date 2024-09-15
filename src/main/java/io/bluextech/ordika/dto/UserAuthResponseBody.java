package io.bluextech.ordika.dto;
/* Created by limxuanhui on 12/8/24 */

import io.bluextech.ordika.models.User;

public record UserAuthResponseBody(User user, String accessToken, String refreshToken, String googlePlacesApiKey) {
    public UserAuthResponseBody {
        if (user == null
                || accessToken == null || accessToken.equals("")
                || refreshToken == null || refreshToken.equals("")
                || googlePlacesApiKey == null || googlePlacesApiKey.equals("")) {
            throw new IllegalArgumentException("Record arguments are invalid. User: " + user + " | accessToken: "
                    + accessToken + " | refreshToken: " + refreshToken + " | googlePlacesApiKey: " + googlePlacesApiKey);
        }
    }
}
