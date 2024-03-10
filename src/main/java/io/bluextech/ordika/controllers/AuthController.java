package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/23 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.bluextech.ordika.dto.UserAuthRequestBody;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService authService;
//    private String s = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjkzNDFkZWRlZWUyZDE4NjliNjU3ZmE5MzAzMDAwODJmZTI2YjNkOTIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIyNDAwMTYzMTY2NzEtYmJiOTZzZ3U1aTMxYWhzZHJvODlmaTd2YTFkcjUwcjUuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIyNDAwMTYzMTY2NzEtYmJiOTZzZ3U1aTMxYWhzZHJvODlmaTd2YTFkcjUwcjUuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTY5MDA2MTgwMTgyMDA3NDMzMjkiLCJlbWFpbCI6Im9yZGlrYS4xN0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXRfaGFzaCI6IlVuNlJDRG9SZkpWVHVKek1sOWpidlEiLCJub25jZSI6IkhoemJjT01DVEZQRU5TTTVibnNmOXl2QnN2dUc4UUtTWHJLQW1Rc0d3d2siLCJuYW1lIjoiSm9zZXBoIExpbSIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQWNIVHRjaGpBWnVIWXlPVzZhcExZemEta3oyd25acnVpaVVZYk9RY0NpS1JPWFc1dz1zOTYtYyIsImdpdmVuX25hbWUiOiJKb3NlcGgiLCJmYW1pbHlfbmFtZSI6IkxpbSIsImxvY2FsZSI6ImVuLUdCIiwiaWF0IjoxNjg5MDQ0MTU5LCJleHAiOjE2ODkwNDc3NTl9.fdQw8DDDz_nx-JG1MxBDaa3KZHurAiV-ai3PPH8EuF84e6RnJHbaL4JFW6FCauWpw_W9KD3SMxeCmoTwVbD9C8_Nty-Imzp8TRtTNoYEyRCa-Cyw2Sv6zMuhuPcazfg2s1-p9lyD30aUj5KhyXmYhWm_6ZWB7WXp3NhBrlgFmKQMgpfvmKK0hkokddzQSKcpY0_BjkUozXb26kx5WdCmTrC2PoPh7ymkSyGuPplei7UtYjs3tdzbRaKrEWNEup7grZhh6gKJqRB9PTiv-nevWl8kngl8s9ce6nqEPRLSo3OqvvmX2XWL48rwgqafOWrter7ZTsb6JwyRNZ7fPyXbeg";

    @PostMapping("/signin")
//    public User signin(@JsonArg("user") User user, @JsonArg("id_token") String idToken)
    public User signin(@RequestBody UserAuthRequestBody body)
            throws GeneralSecurityException, IOException {
        User user = body.getUser();
        String idToken = body.getIdToken();
        final GoogleIdToken verifiedIdToken = authService.verifyIdToken(idToken);
        System.out.println("user: " + user);
        System.out.println(idToken);
        if (verifiedIdToken != null) {
            final GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
            final String subject = payload.getSubject();

            // Check in repository if subject exists i.e. already have an account
            // Change to: Check in cognito if subject exists
            // Also get an access token from cognito
            if (authService.checkIfUserExists(subject)) {
                return authService.fetchUser(subject);
            } else {
                // Subject does not exist in repository; create new account
                // Change to: create new entry for user in cognito
                return authService.createNewUser(user);
            }
        }
        return null;
    }

}
