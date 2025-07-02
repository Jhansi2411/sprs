package com.example.sprs.repository;

import com.example.sprs.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Find user by username and role (used during login)
    Optional<User> findByUsernameAndRole(String username, User.Role role);

    // Find user by username
    Optional<User> findByUsername(String username);

    // Find all users by role (STUDENT, EMPLOYEE, ADMIN)
    List<User> findByRole(User.Role role);

    // Find users by role and active status
    List<User> findByRoleAndIsActive(User.Role role, boolean isActive);

    // Check if username is already taken
    boolean existsByUsername(String username);

    // Check if email is already used in profile
    boolean existsByProfileEmail(String email);

    // Check if roll number is already used in profile
    boolean existsByProfileRollNo(String rollNo);
}
