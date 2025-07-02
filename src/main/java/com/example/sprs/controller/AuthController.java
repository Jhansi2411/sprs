package com.example.sprs.controller;

import com.example.sprs.dto.LoginRequest;
import com.example.sprs.dto.LoginResponse;
import com.example.sprs.dto.RegisterRequest;
import com.example.sprs.dto.ApiResponse;
import com.example.sprs.model.User;
import com.example.sprs.security.JwtUtil;
import com.example.sprs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword(),
                    loginRequest.getRole()
            );

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String token = jwtUtil.generateToken(
                        user.getUsername(),
                        user.getRole().name(),
                        user.getId()
                );

                LoginResponse response = new LoginResponse(user, token);
                return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Invalid credentials", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Authentication failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());
            user.setRole(registerRequest.getRole());
            user.setProfile(registerRequest.getProfile());

            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", createdUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Registration failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved", userOpt.get()));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving profile: " + e.getMessage(), null));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User.Profile profile) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                User updatedUser = userService.updateUserProfile(userOpt.get().getId(), profile);
                return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", updatedUser));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error updating profile: " + e.getMessage(), null));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                userService.changePassword(
                        userOpt.get().getId(),
                        request.getCurrentPassword(),
                        request.getNewPassword()
                );
                return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "User not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error changing password: " + e.getMessage(), null));
        }
    }

    // Inner class for change password request
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
