package io.bluextech.ordika.configs;

import io.bluextech.ordika.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    private final Region region = Region.AP_SOUTHEAST_1;
    private final TableSchema<User> userTableSchema = TableSchema.fromBean(User.class);
    private final TableSchema<BaseMetadata> baseMetadataTableSchema = TableSchema.fromBean(BaseMetadata.class);
    private final TableSchema<FeedMetadata> feedMetadataTableSchema = TableSchema.fromBean(FeedMetadata.class);
    private final TableSchema<FeedItem> feedItemTableSchema = TableSchema.fromBean(FeedItem.class);
    private final TableSchema<TaleMetadata> taleMetadataTableSchema = TableSchema.fromBean(TaleMetadata.class);
    private final TableSchema<Route> routeTableSchema = TableSchema.fromBean(Route.class);
    private final TableSchema<StoryItem> storyItemTableSchema = TableSchema.fromBean(StoryItem.class);
    @Value("${amazon.aws.dynamodb.ENDPOINT}")
    private String ENDPOINT;
    @Value("${amazon.aws.dynamodb.TABLE_NAME}")
    private String TABLE_NAME;
    @Value("${amazon.aws.dynamodb.ACCESS_KEY_ID}")
    private String ACCESS_KEY_ID;
    @Value("${amazon.aws.dynamodb.SECRET_ACCESS_KEY}")
    private String SECRET_ACCESS_KEY;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        System.out.println("DynamoDbEnhancedClient bean created");
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);

        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .endpointOverride(URI.create(ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<BaseMetadata> baseMetadataTable() {
        System.out.println("DynamoDbTable<BaseMetadata> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, baseMetadataTableSchema);
    }

    @Bean
    public DynamoDbTable<User> userTable() {
        System.out.println("DynamoDbTable<User> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, userTableSchema);
    }

    @Bean
    public DynamoDbTable<FeedMetadata> feedMetadataTable() {
        System.out.println("DynamoDbTable<FeedMetadata> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, feedMetadataTableSchema);
    }

    @Bean
    public DynamoDbTable<FeedItem> feedItemTable() {
        System.out.println("DynamoDbTable<FeedItem> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, feedItemTableSchema);
    }

    @Bean
    public DynamoDbTable<TaleMetadata> taleMetadataTable() {
        System.out.println("DynamoDbTable<TaleMetadata> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, taleMetadataTableSchema);
    }

    @Bean
    public DynamoDbTable<Route> routeTable() {
        System.out.println("DynamoDbTable<Route> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, routeTableSchema);
    }

    @Bean
    public DynamoDbTable<StoryItem> storyItemTable() {
        System.out.println("DynamoDbTable<StoryItem> bean created");
        return dynamoDbEnhancedClient().table(TABLE_NAME, storyItemTableSchema);
    }

}