package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/7/23 */

import io.bluextech.ordika.models.Media;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
import io.bluextech.ordika.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    private final int MIN_UPDATE_NAME_INTERVAL_DAYS = 7;
    private final int MIN_UPDATE_HANDLE_INTERVAL_DAYS = 30;
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

    public User updateUserProfile(String userId, Media avatar, String name, String handle, String bio) throws Exception {
        User updatedUser = getUserByUserId(userId);
        Instant now = Instant.now();
        updatedUser.setId(null);
        updatedUser.setEmail(null);
        updatedUser.setCreatedAt(null);
        updatedUser.setIsDeactivated(null);
        updatedUser.setName(name);
        updatedUser.setHandle(handle);
        updatedUser.setBio(bio);
        updatedUser.setAvatar(avatar);

        if (avatar != null) {
            updatedUser.setLastUpdatedAvatarAt(now);
        }

        if (name != null) {
            boolean canUpdateName = updatedUser.getLastUpdatedNameAt() == null || updatedUser.getLastUpdatedNameAt()
                    .plus(MIN_UPDATE_NAME_INTERVAL_DAYS, ChronoUnit.DAYS)
                    .isBefore(now);

            if (!canUpdateName) {
                throw new Exception("Name can only be changed every " + MIN_UPDATE_NAME_INTERVAL_DAYS + " days.");
            }
            updatedUser.setLastUpdatedNameAt(now);
        }

        if (handle != null) {
            boolean canUpdateHandle = updatedUser.getLastUpdatedHandleAt() == null || updatedUser.getLastUpdatedHandleAt()
                    .plus(MIN_UPDATE_HANDLE_INTERVAL_DAYS, ChronoUnit.DAYS)
                    .isBefore(now);

            if (!canUpdateHandle) {
                throw new Exception("Handle can only be changed every " + MIN_UPDATE_HANDLE_INTERVAL_DAYS + " days.");
            }
            updatedUser.setLastUpdatedHandleAt(now);
        }

        if (bio != null) {
            updatedUser.setLastUpdatedBioAt(now);
        }

        return updateUser(updatedUser);
    }

    public User activateUserByUserId(String userId) {
        User updatedUser = getUserByUserId(userId);
        updatedUser.setId(null);
        updatedUser.setName(null);
        updatedUser.setHandle(null);
        updatedUser.setEmail(null);
        updatedUser.setBio(null);
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
        updatedUser.setBio(null);
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
