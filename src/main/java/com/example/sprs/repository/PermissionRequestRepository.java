package com.example.sprs.repository;

import com.example.sprs.model.PermissionRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRequestRepository extends MongoRepository<PermissionRequest, String> {
    List<PermissionRequest> findByUserId(String userId);
    List<PermissionRequest> findByStatus(PermissionRequest.RequestStatus status);
    List<PermissionRequest> findByStatusOrderBySubmittedAtDesc(PermissionRequest.RequestStatus status);
    List<PermissionRequest> findAllByOrderByCreatedAtDesc();
}