package com.example.sprs.dto;

import com.example.sprs.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private User.Role role;

    @NotNull(message = "Profile is required")
    private User.Profile profile;

    // Constructors
    public RegisterRequest() {}

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public User.Profile getProfile() { return profile; }
    public void setProfile(User.Profile profile) { this.profile = profile; }
}