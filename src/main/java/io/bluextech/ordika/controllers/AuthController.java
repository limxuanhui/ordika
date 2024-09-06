package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 23/8/24 */

import io.bluextech.ordika.dto.RefreshTokensRequestBody;
import io.bluextech.ordika.dto.UserAuthRequestBody;
import io.bluextech.ordika.dto.UserAuthResponseBody;
import io.bluextech.ordika.models.AuthUser;
import io.bluextech.ordika.services.AuthService;
import io.bluextech.ordika.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<UserAuthResponseBody> signIn(@RequestBody UserAuthRequestBody body) throws GeneralSecurityException, IOException {
        AuthUser authUser = authService.authenticate(body.user(), body.idToken());
        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new UserAuthResponseBody(authUser.getUser(), authUser.getAccessToken(), authUser.getRefreshToken()));
    }

    @PostMapping("/refresh-tokens")
    public ResponseEntity<?> refreshTokens(@RequestBody RefreshTokensRequestBody body) {
        if (body.refreshToken() == null || body.userId() == null
                || body.refreshToken().isEmpty() || body.userId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token or user id is empty/null.");
        }

        return ResponseEntity.ok(authService.refreshTokens(body.refreshToken(), body.userId()));
    }

}
