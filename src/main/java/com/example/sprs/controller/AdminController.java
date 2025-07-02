package com.example.sprs.controller;

import com.example.sprs.dto.ApiResponse;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

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

    @GetMapping("/requests/accepted")
    public ResponseEntity<?> getAcceptedRequests() {
        try {
            List<Request> acceptedRequests = requestService.getAcceptedRequestsForAdmin();
            return ResponseEntity.ok(new ApiResponse<>(true, "Accepted requests retrieved successfully", acceptedRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving accepted requests: " + e.getMessage(), null));
        }
    }

    @GetMapping("/requests/{id}")
    public ResponseEntity<?> getRequest(@PathVariable String id) {
        try {
            Optional<Request> requestOpt = requestService.findById(id);
            if (requestOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Request retrieved successfully", requestOpt.get()));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Request not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving request: " + e.getMessage(), null));
        }
    }

    @PostMapping("/requests/{id}/print")
    public ResponseEntity<?> markRequestAsPrinted(@PathVariable String id) {
        try {
            User currentUser = getCurrentUser();
            Request completedRequest = requestService.markRequestAsPrinted(id, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Request marked as printed and completed", completedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error marking request as printed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/requests/completed")
    public ResponseEntity<?> getCompletedRequests() {
        try {
            List<Request> completedRequests = requestService.getCompletedRequests();
            return ResponseEntity.ok(new ApiResponse<>(true, "Completed requests retrieved successfully", completedRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving completed requests: " + e.getMessage(), null));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) User.Role role) {
        try {
            List<User> users = (role != null)
                    ? userService.findByRole(role)
                    : userService.getAllActiveUsers();

            return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving users: " + e.getMessage(), null));
        }
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable String id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "User deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error deactivating user: " + e.getMessage(), null));
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        try {
            User currentUser = getCurrentUser();

            Page<Notification> notifications = unreadOnly
                    ? notificationService.getUnreadNotifications(currentUser, page, size)
                    : notificationService.getUserNotifications(currentUser, page, size);

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

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            User currentUser = getCurrentUser();

            List<Request> acceptedRequests = requestService.getAcceptedRequestsForAdmin();
            Page<Notification> notifications = notificationService.getUserNotifications(currentUser, 0, 5);
            long unreadCount = notificationService.getUnreadCount(currentUser);

            AdminDashboardData dashboard = new AdminDashboardData();
            dashboard.setAcceptedRequests(acceptedRequests.subList(0, Math.min(10, acceptedRequests.size())));
            dashboard.setRecentNotifications(notifications.getContent());
            dashboard.setUnreadNotifications(unreadCount);
            dashboard.setTotalAcceptedRequests(acceptedRequests.size());

            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving dashboard: " + e.getMessage(), null));
        }
    }

    // Inner class for admin dashboard data
    public static class AdminDashboardData {
        private List<Request> acceptedRequests;
        private List<Notification> recentNotifications;
        private long unreadNotifications;
        private int totalAcceptedRequests;

        public List<Request> getAcceptedRequests() {
            return acceptedRequests;
        }

        public void setAcceptedRequests(List<Request> acceptedRequests) {
            this.acceptedRequests = acceptedRequests;
        }

        public List<Notification> getRecentNotifications() {
            return recentNotifications;
        }

        public void setRecentNotifications(List<Notification> recentNotifications) {
            this.recentNotifications = recentNotifications;
        }

        public long getUnreadNotifications() {
            return unreadNotifications;
        }

        public void setUnreadNotifications(long unreadNotifications) {
            this.unreadNotifications = unreadNotifications;
        }

        public int getTotalAcceptedRequests() {
            return totalAcceptedRequests;
        }

        public void setTotalAcceptedRequests(int totalAcceptedRequests) {
            this.totalAcceptedRequests = totalAcceptedRequests;
        }
    }
}
