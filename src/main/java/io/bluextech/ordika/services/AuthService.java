package io.bluextech.ordika.services;
/* Created by limxuanhui on 22/8/24 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.bluextech.ordika.models.AuthUser;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {

    @Value("${google.client.CLIENT_ID}")
    private String CLIENT_ID;
    private final HttpTransport httpTransport = new NetHttpTransport();
    private final JsonFactory jsonFactory = new JacksonFactory();
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
            // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(CLIENT_ID))
            // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build();
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public GoogleIdToken verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        return verifier.verify(idToken);
    }

    public AuthUser authenticate(User user, String idToken) throws GeneralSecurityException, IOException {
        final GoogleIdToken verifiedIdToken = verifyIdToken(idToken);
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        if (verifiedIdToken == null) {
            return null;
        }

        final GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
        final String subject = payload.getSubject();

        // Check in repository if subject exists i.e. already have an account
        final User existingUser = userService.getUserByUserId(subject);
        if (existingUser != null) {
            if (existingUser.getIsDeactivated()) {
                System.out.println("Activating user...");
                final UserDeletionInfo userDeletionInfo = userService.checkForUserDeletionRequest(existingUser.getId());
                if (userDeletionInfo != null) {
                    userService.removeUserDeletionRequest(existingUser.getId());
                }
                final User activatedUser = userService.activateUserByUserId(existingUser.getId());
                return new AuthUser(activatedUser, accessToken, refreshToken);
            }
            return new AuthUser(existingUser, accessToken, refreshToken);
        } else {
            // Subject does not exist in repository; create new account
            final User newUser = userService.createUser(user);
            return new AuthUser(newUser, accessToken, refreshToken);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AuthUser(userService.getUserByUserId(username));
    }

}
