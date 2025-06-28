package com.example.sprs.service;

import com.example.sprs.dto.RegisterRequest;
import com.example.sprs.model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User getCurrentUser();
}