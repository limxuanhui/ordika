package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 23/8/24 */

import io.bluextech.ordika.dto.UserAuthRequestBody;
import io.bluextech.ordika.dto.UserAuthResponseBody;
import io.bluextech.ordika.models.AuthUser;
import io.bluextech.ordika.services.AuthService;
import io.bluextech.ordika.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public Optional<UserAuthResponseBody> signIn(@RequestBody UserAuthRequestBody body) throws GeneralSecurityException, IOException {
        AuthUser authUser = authService.authenticate(body.user(), body.idToken());
        UserAuthResponseBody response = null;
        if (authUser != null) {
            response = new UserAuthResponseBody(authUser.getUser(), authUser.getAccessToken(), authUser.getRefreshToken());
        }

        return Optional.ofNullable(response);
    }

}


//        User user = body.user();
//        String idToken = body.idToken();
//        final GoogleIdToken verifiedIdToken = authService.verifyIdToken(idToken);
//
//        if (verifiedIdToken != null) {
//            final GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
//            final String subject = payload.getSubject();
//
//            // Check in repository if subject exists i.e. already have an account
//            final User existingUser = userService.getUserByUserId(subject);
//            if (existingUser != null) {
//                if (existingUser.getIsDeactivated()) {
//                    System.out.println("Activating user...");
//                    final UserDeletionInfo userDeletionInfo = userService.checkForUserDeletionRequest(existingUser.getId());
//                    if (userDeletionInfo != null) {
//                        userService.removeUserDeletionRequest(existingUser.getId());
//                    }
//                    final User activatedUser = userService.activateUserByUserId(existingUser.getId());
//                    return new UserAuthResponseBody(activatedUser, idToken, "");
//                }
//                return new UserAuthResponseBody(existingUser, idToken, "test-access-token");
//            } else {
//                // Subject does not exist in repository; create new account
//                final User newUser = userService.createUser(user);
//                return new UserAuthResponseBody(newUser, idToken, "");
//            }
//        }
//        return null;