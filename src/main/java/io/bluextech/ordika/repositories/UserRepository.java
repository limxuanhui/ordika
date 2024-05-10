package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 13/7/23 */

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bluextech.ordika.models.*;
import io.bluextech.ordika.services.TaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDbTable<User> userTable;
    @Autowired
    private DynamoDbTable<BaseMetadata> baseMetadataTable;
    @Autowired
    private DynamoDbTable<FeedMetadata> feedMetadataTable;
    @Autowired
    private DynamoDbTable<TaleMetadata> taleMetadataTable;
    @Autowired
    private DynamoDbTable<UserDeletionInfo> userDeletionInfoTable;
    @Autowired
    private TaleService taleService;

    public User getUserMetadataByUserId(String userId) {
        System.out.println("Finding user with userId: " + userId);
        return userTable.getItem(Key.builder()
                .partitionValue(User.PK_PREFIX + userId)
                .sortValue(User.SK_PREFIX)
                .build()
        );
    }

    public User createUser(User user) {
        try {
            user.setPK(User.PK_PREFIX + user.getId());
            user.setSK(User.SK_PREFIX);
            userTable.putItem(user);
        } catch (RuntimeException e) {
            System.out.println("Error creating user: " + e.getMessage());
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