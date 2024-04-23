package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/23 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.bluextech.ordika.dto.UserAuthRequestBody;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public User getUserByUserId(@PathVariable String userId) {
        User user = userService.getUserByUserId(userId);
        return user;
    }

    @PostMapping("/auth/signin")
    public User signin(@RequestBody UserAuthRequestBody body) throws GeneralSecurityException, IOException {
        User user = body.getUser();
        String idToken = body.getIdToken();
        final GoogleIdToken verifiedIdToken = userService.verifyIdToken(idToken);
        System.out.println("user: " + user);
        System.out.println(idToken);
        if (verifiedIdToken != null) {
            final GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
            final String subject = payload.getSubject();

            // Check in repository if subject exists i.e. already have an account
            User existingUser = userService.getUserByUserId(subject);
            if (existingUser != null) {
                return existingUser;
            } else {
                // Subject does not exist in repository; create new account
                return userService.createUser(user);
            }
        }
        return null;
    }

}
