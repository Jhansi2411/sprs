package com.example.sprs.service;

import com.example.sprs.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(String id);
    Optional<User> getByUsername(String username);
    Optional<User> updateUser(String id, User updatedUser);
    boolean deleteUser(String id);
    void deleteAllUsers();
    Optional<User> changeUserPassword(String id, String newPassword);
}
