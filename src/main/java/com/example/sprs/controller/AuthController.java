package com.example.sprs.controller;

import com.example.sprs.dto.AuthResponse;
import com.example.sprs.dto.LoginRequest;
import com.example.sprs.dto.RegisterRequest;
import com.example.sprs.model.User;
import com.example.sprs.service.JwtService;
import com.example.sprs.service.PasswordService;
import com.example.sprs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.registerUser(registerRequest);


            // Remove password from response
            user.setPassword(null);

            return ResponseEntity.ok(( user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordService.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            if (!user.getRole().equals(loginRequest.getRole())) {
                return ResponseEntity.badRequest().body("Invalid role");
            }

            String token = jwtService.generateToken(user.getUsername());

            // Remove password from response
            user.setPassword(null);

            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}