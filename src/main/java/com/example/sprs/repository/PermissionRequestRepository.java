package com.example.sprs.repository;

import com.example.sprs.model.PermissionRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PermissionRequestRepository extends MongoRepository<PermissionRequest, String> {

    // 🔍 Enables GET /api/requests/student/{studentId}
    List<PermissionRequest> findByStudentId(String studentId);
}
