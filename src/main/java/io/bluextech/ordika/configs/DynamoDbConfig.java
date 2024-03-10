package io.bluextech.ordika.configs;
/* Created by limxuanhui on 19/1/24 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties("amazon.aws.dynamodb")
public class DynamoDbConfig {

    private String ACCESS_KEY;
    private String SECRET_KEY;
    private String endpoint;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public void createTable() {
//        dynamoDbEnhancedClient().table("sundaytable", ).getItem("USER#123");
    }

}
