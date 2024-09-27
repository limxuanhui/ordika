package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/8/24 */

import io.bluextech.ordika.configs.GooglePlacesApiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SecretService {

    private final GooglePlacesApiConfig googlePlacesApiConfig;

    public String getGooglePlacesApiKey() {
        return googlePlacesApiConfig.getAPI_KEY();
    }

}
