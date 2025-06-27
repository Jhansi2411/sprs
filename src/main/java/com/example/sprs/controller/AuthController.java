package com.example.sprs.controller;

import com.example.sprs.service.UserService;
import com.example.sprs.util.JwtUtil;
import com.example.sprs.config.PasswordService;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordService passwordService;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordService passwordService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordService = passwordService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        log.debug("Login attempt for username: {}", req.getUsername());

        return userService.getByUsername(req.getUsername())
                .map(user -> {
                    if (passwordService.matches(req.getPassword(), user.getPassword())) {
                        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                        log.info("✅ Login successful for user: {}", user.getUsername());
                        return ResponseEntity.ok(new AuthResponse(token, user.getRole()));
                    } else {
                        log.warn("❌ Incorrect password for user: {}", user.getUsername());
                        return ResponseEntity.status(401)
                                .body(new AuthResponse("Invalid username or password"));
                    }
                })
                .orElseGet(() -> {
                    log.warn("❌ Login failed - user '{}' not found", req.getUsername());
                    return ResponseEntity.status(401)
                            .body(new AuthResponse("Invalid username or password"));
                });
    }

    // --- DTOs (Inner Classes) ---

    @Data
    public static class AuthRequest {
        private String username;
        private String password;
    }

    public static class AuthResponse {
        private final String token;
        private final String role;

        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        // Constructor for error case
        public AuthResponse(String errorMessage) {
            this.token = null;
            this.role = errorMessage;
        }

        public String getToken() {
            return token;
        }

        public String getRole() {
            return role;
        }
    }
}
