package com.example.sprs.controller;

import com.example.sprs.dto.ApiResponse;
import com.example.sprs.dto.ReviewRequestDto;
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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

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

    @GetMapping("/requests/pending")
    public ResponseEntity<?> getPendingRequests() {
        try {
            List<Request> pendingRequests = requestService.getPendingRequestsForEmployee();
            return ResponseEntity.ok(new ApiResponse<>(true, "Pending requests retrieved successfully", pendingRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving pending requests: " + e.getMessage(), null));
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

    @PostMapping("/requests/{id}/review")
    public ResponseEntity<?> reviewRequest(@PathVariable String id, @Valid @RequestBody ReviewRequestDto reviewDto) {
        try {
            User currentUser = getCurrentUser();

            // ✅ Convert enum to string using .name()
            Request reviewedRequest = requestService.employeeReviewRequest(
                    id,
                    currentUser.getId(),
                    reviewDto.getAction().name(),  // ✅ FIXED LINE
                    reviewDto.getComments(),
                    reviewDto.getRejectionReason()
            );

            // ✅ Also comparing enums correctly
            String message = ReviewRequestDto.Action.ACCEPTED.equals(reviewDto.getAction()) ?
                    "Request accepted successfully" : "Request rejected successfully";

            return ResponseEntity.ok(new ApiResponse<>(true, message, reviewedRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error reviewing request: " + e.getMessage(), null));
        }
    }


    @GetMapping("/requests/reviewed")
    public ResponseEntity<?> getReviewedRequests() {
        try {
            User currentUser = getCurrentUser();
            List<Request> reviewedRequests = requestService.getRequestsReviewedByEmployee(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Reviewed requests retrieved successfully", reviewedRequests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving reviewed requests: " + e.getMessage(), null));
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

            List<Request> pendingRequests = requestService.getPendingRequestsForEmployee();
            Page<Notification> notifications = notificationService.getUserNotifications(currentUser, 0, 5);
            long unreadCount = notificationService.getUnreadCount(currentUser);

            EmployeeDashboardData dashboard = new EmployeeDashboardData();
            dashboard.setPendingRequests(pendingRequests.subList(0, Math.min(10, pendingRequests.size())));
            dashboard.setRecentNotifications(notifications.getContent());
            dashboard.setUnreadNotifications(unreadCount);
            dashboard.setTotalPendingRequests(pendingRequests.size());

            return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Error retrieving dashboard: " + e.getMessage(), null));
        }
    }

    // Inner class for employee dashboard data
    public static class EmployeeDashboardData {
        private List<Request> pendingRequests;
        private List<Notification> recentNotifications;
        private long unreadNotifications;
        private int totalPendingRequests;

        public List<Request> getPendingRequests() {
            return pendingRequests;
        }

        public void setPendingRequests(List<Request> pendingRequests) {
            this.pendingRequests = pendingRequests;
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

        public int getTotalPendingRequests() {
            return totalPendingRequests;
        }

        public void setTotalPendingRequests(int totalPendingRequests) {
            this.totalPendingRequests = totalPendingRequests;
        }
    }
}
