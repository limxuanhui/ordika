package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 13/7/23 */

import io.bluextech.ordika.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDbTable<User> userTable;

    public User findUserMetadataByUserId(String userId) {
        System.out.println("Finding user with userId: " + userId);
        return userTable.getItem(
                Key.builder()
                        .partitionValue("USER#" + userId)
                        .sortValue("#METADATA")
                        .build()
        );
    }

    public List<User> findUsersMetadataPage() {
        return null;
    }

    public User createUser(User user) {
        try {
            user.setPK("USER#" + user.getId());
            user.setSK("#METADATA");
            userTable.putItem(user);
        } catch (RuntimeException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        return user;
    }

}