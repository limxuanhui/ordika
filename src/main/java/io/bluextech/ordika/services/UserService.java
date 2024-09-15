package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/7/23 */

import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
import io.bluextech.ordika.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedService feedService;
    @Autowired
    private TaleService taleService;

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User getUserByUserId(String userId) {
        return userRepository.getUserMetadataByUserId(userId);
    }

    public User updateUser(User user) {
        return userRepository.updateUserMetadata(user);
    }

    public User activateUserByUserId(String userId) {
        User updatedUser = getUserByUserId(userId);
        updatedUser.setId(null);
        updatedUser.setName(null);
        updatedUser.setHandle(null);
        updatedUser.setEmail(null);
        updatedUser.setAvatar(null);
        updatedUser.setCreatedAt(null);
        updatedUser.setIsDeactivated(false);

        feedService.activateAllFeedsByUserId(userId);
        taleService.activateAllTalesByUserId(userId);

        return updateUser(updatedUser);
    }

    public User deactivateUserByUserId(String userId) {
        User updatedUser = getUserByUserId(userId);
        updatedUser.setId(null);
        updatedUser.setName(null);
        updatedUser.setHandle(null);
        updatedUser.setEmail(null);
        updatedUser.setAvatar(null);
        updatedUser.setCreatedAt(null);
        updatedUser.setIsDeactivated(true);

        feedService.deactivateAllFeedsByUserId(userId);
        taleService.deactivateAllTalesByUserId(userId);

        return updateUser(updatedUser);
    }

    public UserDeletionInfo saveUserDeletionRequest(String userId) {
        UserDeletionInfo userDeletionInfo = new UserDeletionInfo(userId, Instant.now());
        return userRepository.saveUserDeletionInfo(userDeletionInfo);
    }

    public UserDeletionInfo removeUserDeletionRequest(String userId) {
        return userRepository.deleteUserDeletionInfoByUserId(userId);
    }

    public UserDeletionInfo checkForUserDeletionRequest(String userId) {
        return userRepository.getUserDeletionInfoByUserId(userId);
    }

}
