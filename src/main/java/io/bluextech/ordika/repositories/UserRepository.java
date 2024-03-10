package io.bluextech.ordika.repositories;
/* Created by limxuanhui on 13/7/23 */

import io.bluextech.ordika.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private DynamoDbTable<User> userTable;

    public User findUserMetadataById(String id) {
        return userTable.query(QueryConditional.sortBeginsWith(
                        Key.builder()
                                .partitionValue("USER#" + id)
                                .sortValue("#METADATA")
                                .build())
                )
                .items()
                .stream()
                .findFirst()
                .orElse(null);

    }

    public List<User> findUsersMetadata() {
        return null;
    }

    public User createUser(User user) {
        PutItemEnhancedRequest<User> request = PutItemEnhancedRequest.builder(User.class)
                .conditionExpression(Expression.builder().build())
                .build();
        try {
            userTable.putItem(request);
        } catch (RuntimeException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }

        return user;
    }

}