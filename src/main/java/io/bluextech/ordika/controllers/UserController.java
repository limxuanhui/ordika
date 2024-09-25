package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/23 */

import io.bluextech.ordika.dto.UpdateUserProfileRequestBody;
import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
import io.bluextech.ordika.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public User getUserByUserId(@PathVariable String userId) {

        User u = userService.getUserByUserId(userId);
        System.out.println(u);
        return u;
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserProfileRequestBody body) {
        try {
            User updatedUser = userService.updateUserProfile(body.userId(), body.avatar(), body.name(), body.handle(), body.bio());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        }
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
