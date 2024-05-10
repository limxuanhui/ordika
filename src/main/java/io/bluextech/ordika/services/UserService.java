package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/7/23 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.bluextech.ordika.models.FeedMetadata;
import io.bluextech.ordika.models.TaleMetadata;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedService feedService;
    @Autowired
    private TaleService taleService;
    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList("240016316671-bbb96sgu5i31ahsdro89fi7va1dr50r5.apps.googleusercontent.com"))
            // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();

    public GoogleIdToken verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        return verifier.verify(idToken);
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User getUserByUserId(String userId) {
        System.out.println("Getting user...");
        return userRepository.getUserMetadataByUserId(userId);
    }

    public User updateUser(User user) {
        return null;
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


        List<FeedMetadata> allFeedMetadataList = feedService.activateAllFeedsByUserId(userId);
        List<TaleMetadata> allTaleMetadataList = taleService.activateAllTalesByUserId(userId);
//        BaseMetadata itineraryMetadata;


        return userRepository.updateUserMetadata(updatedUser);
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

        List<FeedMetadata> allFeedMetadataList = feedService.deactivateAllFeedsByUserId(userId);
        List<TaleMetadata> allTaleMetadataList = taleService.deactivateAllTalesByUserId(userId);
//        BaseMetadata itineraryMetadata;

        return userRepository.updateUserMetadata(updatedUser);
    }

    public User deleteUserByUserId(String userId) throws JsonProcessingException {
        return userRepository.deleteUserByUserId(userId);
    }

}
