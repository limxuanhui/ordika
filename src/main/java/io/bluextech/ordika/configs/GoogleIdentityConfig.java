package io.bluextech.ordika.configs;
/* Created by limxuanhui on 26/8/24 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleIdentityConfig {

    private final String CLIENT_ID;

    public GoogleIdentityConfig(@Value("${google.client.CLIENT_ID}") String CLIENT_ID) {
        this.CLIENT_ID = CLIENT_ID;
    }

    @Bean
    public HttpTransport httpTransport() {
        return new NetHttpTransport();
    }

    @Bean
    public JsonFactory jsonFactory() {
        return new JacksonFactory();
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(HttpTransport httpTransport, JsonFactory jsonFactory) {
        return new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
    }

}
