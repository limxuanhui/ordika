package io.bluextech.ordika.configs;
/* Created by limxuanhui on 24/1/23 */

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${google.api.directions.API_KEY}")
    private String API_KEY;

    @Bean
    GeoApiContext geoApiContext() {
        return new GeoApiContext.Builder().apiKey(API_KEY).build();
    }

}
