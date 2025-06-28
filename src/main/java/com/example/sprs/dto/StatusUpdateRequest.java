package com.example.sprs.dto;

import com.example.sprs.model.PermissionRequest;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private PermissionRequest.RequestStatus status;

    private String reviewedBy;

    // Constructors
    public StatusUpdateRequest() {}

    public StatusUpdateRequest(PermissionRequest.RequestStatus status, String reviewedBy) {
        this.status = status;
        this.reviewedBy = reviewedBy;
    }

    // Getters and Setters
    public PermissionRequest.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(PermissionRequest.RequestStatus status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}