package io.bluextech.ordika.controllers;
/* Created by limxuanhui on 10/7/23 */

import io.bluextech.ordika.models.User;
import io.bluextech.ordika.models.UserDeletionInfo;
import io.bluextech.ordika.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public User getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
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
