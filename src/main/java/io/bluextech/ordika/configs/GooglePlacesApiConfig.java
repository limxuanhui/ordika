package io.bluextech.ordika.configs;
/* Created by limxuanhui on 10/7/24 */

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "google.api.places")
public class GooglePlacesApiConfig {

    private String API_KEY;

    public GooglePlacesApiConfig() {
        System.out.println("Google places api config constructor called");
    }

}
