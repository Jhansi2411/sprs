package com.example.sprs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @DBRef
    private User recipient;

    @DBRef
    private User sender;

    @DBRef
    private Request request;

    private NotificationType type;

    private String title;

    private String message;

    private boolean isRead = false;

    private LocalDateTime readAt;

    private Priority priority = Priority.MEDIUM;

    private Metadata metadata;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public Notification() {}

    public Notification(User recipient, User sender, Request request,
                        NotificationType type, String title, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.request = request;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    // Method to mark as read
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Enums
    public enum NotificationType {
        REQUEST_SUBMITTED, REQUEST_ACCEPTED, REQUEST_REJECTED, REQUEST_COMPLETED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    // Nested Class
    public static class Metadata {
        private String requestType;
        private String requestStatus;
        private String actionBy;

        // Constructors
        public Metadata() {}

        public Metadata(String requestType, String requestStatus, String actionBy) {
            this.requestType = requestType;
            this.requestStatus = requestStatus;
            this.actionBy = actionBy;
        }

        // Getters and Setters
        public String getRequestType() { return requestType; }
        public void setRequestType(String requestType) { this.requestType = requestType; }

        public String getRequestStatus() { return requestStatus; }
        public void setRequestStatus(String requestStatus) { this.requestStatus = requestStatus; }

        public String getActionBy() { return actionBy; }
        public void setActionBy(String actionBy) { this.actionBy = actionBy; }
    }
}