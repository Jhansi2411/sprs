package com.example.sprs.controller;

import com.example.sprs.model.User;
import com.example.sprs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /** ✅ Register a new user */
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.createUser(user);
    }

    /** ✅ Get all users */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /** ✅ Get one user by ID */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** ✅ Update an existing user */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** ✅ Delete one user */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        boolean deleted = userService.deleteUser(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /** 🆕 Delete all users (Admin only) */
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.noContent().build();
    }

    /** 🆕 Get user by username (useful for profile fetching) */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return userService.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 🆕 Check if a username exists (frontend validation) */
    @GetMapping("/exists/{username}")
    public boolean usernameExists(@PathVariable String username) {
        return userService.getByUsername(username).isPresent();
    }

    /** 🆕 Change password for a user */
    @PatchMapping("/{id}/password")
    public ResponseEntity<User> changePassword(
            @PathVariable String id,
            @RequestBody PasswordChangeRequest body) {
        return userService.changeUserPassword(id, body.getNewPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** DTO for password updates */
    public static class PasswordChangeRequest {
        private String newPassword;
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
