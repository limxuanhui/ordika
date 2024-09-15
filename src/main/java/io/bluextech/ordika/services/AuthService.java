package io.bluextech.ordika.services;
/* Created by limxuanhui on 22/8/24 */

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.bluextech.ordika.models.AuthUser;
import io.bluextech.ordika.models.TokenPair;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
import io.bluextech.ordika.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final GoogleIdTokenVerifier verifier;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    @Value("${ordika.auth.ACCESS_TOKEN_VALID_DURATION_MS}")
    private Long ACCESS_TOKEN_VALID_DURATION_MS;
    @Value("${ordika.auth.REFRESH_TOKEN_VALID_DURATION_MS}")
    private Long REFRESH_TOKEN_VALID_DURATION_MS;

    public GoogleIdToken verifyIdToken(String idToken) throws GeneralSecurityException, IOException {
        return verifier.verify(idToken);
    }

    public AuthUser authenticate(User user, String idToken) throws GeneralSecurityException, IOException {
        final GoogleIdToken verifiedIdToken = verifyIdToken(idToken);
        if (verifiedIdToken == null) {
            return null;
        }

        final GoogleIdToken.Payload payload = verifiedIdToken.getPayload();
        final String subject = payload.getSubject();

        // Check in repository if subject exists i.e. already have an account
        final User existingUser = userService.getUserByUserId(subject);
        final User authUser;
        if (existingUser != null) {
            if (existingUser.getIsDeactivated()) {
                LOGGER.info("Activating user...");
                final UserDeletionInfo userDeletionInfo = userService.checkForUserDeletionRequest(existingUser.getId());
                if (userDeletionInfo != null) {
                    userService.removeUserDeletionRequest(existingUser.getId());
                }
                authUser = userService.activateUserByUserId(existingUser.getId());
            } else {
                authUser = existingUser;
            }
        } else {
            // Subject does not exist in repository; create new account
            authUser = userService.createUser(user);
        }

        TokenPair tokenPair = generateAuthTokens(authUser.getId());
        return new AuthUser(authUser, tokenPair.accessToken(), tokenPair.refreshToken());
    }

    private TokenPair generateAuthTokens(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ORDIKA_USER");
        claims.put("tokenType", "access");
        String newAccessToken = jwtUtil.createToken(userId, claims, ACCESS_TOKEN_VALID_DURATION_MS);
        claims.put("tokenType", "refresh");
        String newRefreshToken = jwtUtil.createToken(userId, claims, REFRESH_TOKEN_VALID_DURATION_MS);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    public TokenPair refreshTokens(String refreshToken, String userId) {
        if (!jwtUtil.isTokenValid(refreshToken, userId) || !"refresh".equals(jwtUtil.extractCustomClaim(refreshToken, "tokenType"))) {
            LOGGER.error("Invalid token or invalid userId");
            // TODO: throw exception with above message
//            throw new Exception("Refresh token is invalid");
            return null;
        }

        return generateAuthTokens(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AuthUser(userService.getUserByUserId(username));
    }

}
