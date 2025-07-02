package com.example.sprs.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class ReviewRequestDto {

    public enum Action {
        ACCEPTED,
        REJECTED
    }

    @NotNull(message = "Action is required")
    private Action action;

    private String comments;

    private String rejectionReason;

    // Constructors
    public ReviewRequestDto() {}

    public ReviewRequestDto(Action action, String comments, String rejectionReason) {
        this.action = action;
        this.comments = comments;
        this.rejectionReason = rejectionReason;
    }

    // Getters and setters
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
