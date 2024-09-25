package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 13/7/23 */

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bluextech.ordika.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
    @Autowired
    private DynamoDbTable<User> userTable;
    @Autowired
    private DynamoDbTable<FeedMetadata> feedMetadataTable;
    @Autowired
    private DynamoDbTable<UserDeletionInfo> userDeletionInfoTable;
    @Autowired
    private DynamoDbClient dynamoDbClient;
    @Autowired
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public User getUserMetadataByUserId(String userId) {
        LOGGER.info("Finding user with userId: " + userId);
        return userTable.getItem(Key.builder()
                .partitionValue(User.PK_PREFIX + userId)
                .sortValue(User.SK_PREFIX)
                .build()
        );
    }

    public List<User> batchGetUsersByUserIds(Set<String> userIds) {
        List<ReadBatch> readBatches = userIds.stream()
                .map(userId -> ReadBatch.builder(User.class)
                        .mappedTableResource(userTable)
                        .addGetItem(Key.builder()
                                .partitionValue(User.PK_PREFIX + userId)
                                .sortValue(User.SK_PREFIX)
                                .build())
                        .build())
                .toList();
        BatchGetItemEnhancedRequest request = BatchGetItemEnhancedRequest.builder()
                .readBatches(readBatches)
                .build();

        BatchGetResultPageIterable result = dynamoDbEnhancedClient.batchGetItem(request);
        List<User> users = result.resultsForTable(userTable).stream().toList();
        LOGGER.info("Batch got users");
        System.out.println(users);
        return users;
    }

    public User createUser(User user) {
        LOGGER.info("Creating user with userId: " + user.getId());
        try {
            user.setPK(User.PK_PREFIX + user.getId());
            user.setSK(User.SK_PREFIX);
            userTable.putItem(user);
        } catch (RuntimeException e) {
            LOGGER.error("Error creating user: " + e.getMessage());
        }

        return userTable.getItem(Key.builder()
                .partitionValue(user.getPK())
                .sortValue(user.getSK())
                .build());
    }

    public User updateUserMetadata(User user) {
        UpdateItemEnhancedRequest<User> request = UpdateItemEnhancedRequest.builder(User.class)
                .item(user)
                .ignoreNulls(true)
                .build();

        return userTable.updateItem(request);
    }

    public UserDeletionInfo saveUserDeletionInfo(UserDeletionInfo userDeletionInfo) {
        userDeletionInfoTable.putItem(userDeletionInfo);
        return userDeletionInfo;
    }

    public UserDeletionInfo getUserDeletionInfoByUserId(String userId) {
        return userDeletionInfoTable.getItem(Key.builder()
                .partitionValue(UserDeletionInfo.PK_PREFIX)
                .sortValue(UserDeletionInfo.SK_PREFIX + userId)
                .build());
    }

    public UserDeletionInfo deleteUserDeletionInfoByUserId(String userId) {
        UserDeletionInfo userDeletionInfo = getUserDeletionInfoByUserId(userId);
        if (userDeletionInfo != null) {
            userDeletionInfoTable.deleteItem(Key.builder()
                    .partitionValue(UserDeletionInfo.PK_PREFIX)
                    .sortValue(UserDeletionInfo.SK_PREFIX + userId)
                    .build());
        }

        return userDeletionInfo;
    }

    public User deleteUserByUserId(String userId) throws JsonProcessingException {
        List<Feed> userFeedsList = new ArrayList<>();// feedService.deleteAllFeedsByUserId(userId);
        List<Tale> userTalesList = new ArrayList<>();//taleService.deleteAllTalesByUserId(userId);
        TransactWriteItemsEnhancedRequest.Builder transactionalDeleteRequest = TransactWriteItemsEnhancedRequest.builder();

        userFeedsList.forEach(feed -> {
            transactionalDeleteRequest.addDeleteItem(feedMetadataTable, Key.builder()
//                    .partitionValue("FEED#" + feed.).sortValue()
                    .build());
        });
//        User user = getUserMetadataByUserId(userId);

        User deletedUser = userTable.deleteItem(Key.builder()
                .partitionValue(User.PK_PREFIX + userId)
                .sortValue(User.SK_PREFIX)
                .build());

        return deletedUser;
    }

}