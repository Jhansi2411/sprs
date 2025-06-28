package com.example.sprs.service;

import com.example.sprs.dto.StatusUpdateRequest;
import com.example.sprs.model.PermissionRequest;

import java.util.List;
import java.util.Optional;

public interface PermissionRequestService {
    PermissionRequest createRequest(PermissionRequest request);
    Optional<PermissionRequest> findById(String id);
    PermissionRequest updateRequest(String id, PermissionRequest request);
    PermissionRequest generateLetter(String id);
    PermissionRequest submitRequest(String id);
    List<PermissionRequest> getUserRequests(String userId);
    List<PermissionRequest> getAllRequests();
    List<PermissionRequest> getInboxRequests();
    PermissionRequest updateStatus(String id, StatusUpdateRequest statusUpdate);
    void deleteRequest(String id);
}