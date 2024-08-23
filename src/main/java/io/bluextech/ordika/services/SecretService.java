package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/8/24 */

import io.bluextech.ordika.configs.GooglePlacesApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretService {

    @Autowired
    private GooglePlacesApiConfig googlePlacesApiConfig;

    public String getGooglePlacesApiKey() {
        return googlePlacesApiConfig.getAPI_KEY();
    }

}
