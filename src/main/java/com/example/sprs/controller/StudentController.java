package com.example.sprs.controller;

import com.example.sprs.dto.ApiResponse;
import com.example.sprs.dto.CreateRequestDto;
import com.example.sprs.model.Request;
import com.example.sprs.model.User;
import com.example.sprs.model.Notification;
import com.example.sprs.service.RequestService;
import com.example.sprs.service.NotificationService;
import com.example.sprs.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import 	jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(@Valid @RequestBody CreateRequestDto createRequestDto) {
        try {
            User currentUser = getCurrentUser();

            Request request = new Request();
            request.setRequestType(createRequestDto.getRequestType());
            request.setFormData(createRequestDto.getFormData());

            Request createdRequest = requestService.createRequest(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Request created successfully", createdRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error creating request: " + e.getMessage(), null));
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getRequests(@RequestParam(required = false) Request.Status status) {
        try {
            User currentUser = getCurrentUser();

            List<Request> requests;
            if (status != null) {
                requests = requestService.getStudentRequestsByStatus(currentUser.getId(), status);
            } else {
                requests = requestService.getStudentRequests(currentUser.getId());
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Requests retrieved successfully", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving requests: " + e.getMessage(), null));
        }
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<?> getRequest(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            Optional<Request> requestOpt = requestService.findById(id);

            if (requestOpt.isPresent()) {
                Request request = requestOpt.get();
                if (!request.getStudent().getId().equals(currentUser.getId())) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse<>(false, "Access denied", null));
                }
                return ResponseEntity.ok(new ApiResponse<>(true, "Request retrieved successfully", request));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Request not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving request: " + e.getMessage(), null));
        }
    }

    @PutMapping("/requests/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable String id, @Valid @RequestBody CreateRequestDto updateRequestDto) {
        try {
            User currentUser = getCurrentUser();

            Request request = new Request();
            request.setRequestType(updateRequestDto.getRequestType());
            request.setFormData(updateRequestDto.getFormData());

            Request updatedRequest = requestService.updateRequest(id, currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Request updated successfully", updatedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error updating request: " + e.getMessage(), null));
        }
    }

    @PostMapping("/requests/{id}/submit")
    public ResponseEntity<?> submitRequest(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            Request submittedRequest = requestService.generateAndSubmitLetter(id, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Request submitted successfully", submittedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error submitting request: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/requests/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            requestService.deleteRequest(id, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Request deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error deleting request: " + e.getMessage(), null));
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        try {
            User currentUser = getCurrentUser();

            Page<Notification> notifications;
            if (unreadOnly) {
                notifications = notificationService.getUnreadNotifications(currentUser, page, size);
            } else {
                notifications = notificationService.getUserNotifications(currentUser, page, size);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving notifications: " + e.getMessage(), null));
        }
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            Notification notification = notificationService.markAsRead(id, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read", notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error marking notification as read: " + e.getMessage(), null));
        }
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead() {
        try {
            User currentUser = getCurrentUser();
            notificationService.markAllAsRead(currentUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "All notifications marked as read", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error marking notifications as read: " + e.getMessage(), null));
        }
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<?> getUnreadNotificationCount() {
        try {
            User currentUser = getCurrentUser();
            long unreadCount = notificationService.getUnreadCount(currentUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "Unread count retrieved", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving unread count: " + e.getMessage(), null));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            User currentUser = getCurrentUser();

            List<Request> recentRequests = requestService.getStudentRequests(currentUser.getId());
            Page<Notification> notifications = notificationService.getUserNotifications(currentUser, 0, 5);
            long unreadCount = notificationService.getUnreadCount(currentUser);

            DashboardData dashboard = new DashboardData();
            dashboard.setRecentRequests(recentRequests.subList(0, Math.min(5, recentRequests.size())));
            dashboard.setRecentNotifications(notifications.getContent());
            dashboard.setUnreadNotifications(unreadCount);

            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving dashboard: " + e.getMessage(), null));
        }
    }

    // Inner class for dashboard data
    public static class DashboardData {
        private List<Request> recentRequests;
        private List<Notification> recentNotifications;
        private long unreadNotifications;

        // Getters and setters
        public List<Request> getRecentRequests() { return recentRequests; }
        public void setRecentRequests(List<Request> recentRequests) { this.recentRequests = recentRequests; }

        public List<Notification> getRecentNotifications() { return recentNotifications; }
        public void setRecentNotifications(List<Notification> recentNotifications) { this.recentNotifications = recentNotifications; }

        public long getUnreadNotifications() { return unreadNotifications; }
        public void setUnreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
    }
}