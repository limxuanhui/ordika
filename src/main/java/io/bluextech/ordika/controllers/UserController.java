package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/23 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.bluextech.ordika.dto.UserAuthRequestBody;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
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
        return userService.getUserByUserId(userId);
    }

    @PostMapping("/auth/signin")
    public User signIn(@RequestBody UserAuthRequestBody body) throws GeneralSecurityException, IOException {
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
                if (existingUser.getIsDeactivated()) {
                    System.out.println("Activating user...");
                    UserDeletionInfo userDeletionInfo = userService.checkForUserDeletionRequest(existingUser.getId());
                    if (userDeletionInfo != null) {
                        userService.removeUserDeletionRequest(existingUser.getId());
                    }
                    return userService.activateUserByUserId(existingUser.getId());
                }
                return existingUser;
            } else {
                // Subject does not exist in repository; create new account
                return userService.createUser(user);
            }
        }
        return null;
    }

    @PostMapping("/deactivate-accounts/{userId}")
    public User deactivateAccount(@PathVariable String userId) {
        return userService.deactivateUserByUserId(userId);
    }

    @DeleteMapping("/delete-accounts/{userId}")
    public UserDeletionInfo markUserForDeletion(@PathVariable String userId) {
        userService.deactivateUserByUserId(userId);
        return userService.saveUserDeletionRequest(userId);
    }

}
