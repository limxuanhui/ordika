package io.bluextech.ordika.configs;
/* Created by limxuanhui on 24/1/23 */

import com.google.maps.GeoApiContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "google.api.directions")
public class GoogleDirectionsApiConfig {

    private String API_KEY;
    private String UNITS;
    private String MODE;
    private String TRANSIT_MODE;
    private String OUTPUT_FORMAT;
    private String BASE_URL;

    public GoogleDirectionsApiConfig() {
        System.out.println("Google directions api config constructor called");
    }

    @Bean
    public GeoApiContext geoApiContext() {
        return new GeoApiContext.Builder().apiKey(API_KEY).build();
    }

}
