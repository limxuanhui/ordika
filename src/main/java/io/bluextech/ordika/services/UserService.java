package io.bluextech.ordika.services;
/* Created by limxuanhui on 11/7/23 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

//    @Autowired
//    private final MediaService mediaService;

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

    public Boolean checkIfUserExists(String sub) {
        return null;
    }

    public User createNewUser(User user) {
        return null;
    }

    public User fetchUser(String sub) {
        return null;
    }

}
