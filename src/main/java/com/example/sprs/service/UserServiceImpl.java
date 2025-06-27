package com.example.sprs.service;

import com.example.sprs.model.User;
import com.example.sprs.repository.UserRepository;
import com.example.sprs.config.PasswordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordService passwordService;

    @Override
    public User createUser(User user) {
        user.setPassword(passwordService.hashPassword(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public Optional<User> updateUser(String id, User updatedUser) {
        return userRepo.findById(id).map(existing -> {
            existing.setUsername(updatedUser.getUsername());
            existing.setRole(User.Role.valueOf(updatedUser.getRole().toUpperCase()));

            existing.setPassword(passwordService.hashPassword(updatedUser.getPassword()));
            return userRepo.save(existing);
        });
    }

    @Override
    public boolean deleteUser(String id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllUsers() {
        userRepo.deleteAll();
    }

    @Override
    public Optional<User> changeUserPassword(String id, String newPassword) {
        return userRepo.findById(id).map(user -> {
            user.setPassword(passwordService.hashPassword(newPassword));
            return userRepo.save(user);
        });
    }
}
