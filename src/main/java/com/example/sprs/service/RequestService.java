package com.example.sprs.service;

import com.example.sprs.model.Notification;
import com.example.sprs.model.Request;
import com.example.sprs.model.User;
import com.example.sprs.repository.RequestRepository;
import com.example.sprs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public Request createRequest(String studentId, Request request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        request.setStudent(student);
        request.setStatus(Request.Status.DRAFT);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public Optional<Request> findById(String requestId) {
        return requestRepository.findById(requestId);
    }

    public List<Request> getStudentRequests(String studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return requestRepository.findByStudent(student);
    }

    public List<Request> getStudentRequestsByStatus(String studentId, Request.Status status) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return requestRepository.findByStudentAndStatus(student, status);
    }

    public Request updateRequest(String requestId, String studentId, Request updatedRequest) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }

        if (request.getStatus() != Request.Status.DRAFT) {
            throw new RuntimeException("Cannot update submitted request");
        }

        request.setRequestType(updatedRequest.getRequestType());
        request.setFormData(updatedRequest.getFormData());
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public Request generateAndSubmitLetter(String requestId, String studentId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }

        if (request.getStatus() != Request.Status.DRAFT) {
            throw new RuntimeException("Request already submitted");
        }

        request.generateLetter();
        request.setStatus(Request.Status.PENDING);
        request.setUpdatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);

        List<User> employees = userRepository.findByRoleAndIsActive(User.Role.EMPLOYEE, true);
        for (User employee : employees) {
            notificationService.createNotification(
                    employee,
                    request.getStudent(),
                    savedRequest,
                    Notification.NotificationType.REQUEST_SUBMITTED,
                    "New " + request.getRequestType() + " Request",
                    "A new " + request.getRequestType().name().toLowerCase() +
                            " request has been submitted by " + request.getFormData().getName()
            );
        }

        return savedRequest;
    }

    public List<Request> getPendingRequestsForEmployee() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(Request.Status.PENDING);
    }

    public Request employeeReviewRequest(String requestId, String employeeId,
                                         String action, String comments, String rejectionReason) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != Request.Status.PENDING) {
            throw new RuntimeException("Request is not in pending status");
        }

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Request.EmployeeReview review = new Request.EmployeeReview(employeeId, comments, action);
        request.setEmployeeReview(review);

        if ("ACCEPTED".equals(action)) {
            request.setStatus(Request.Status.ACCEPTED);

            notificationService.createNotification(
                    request.getStudent(),
                    employee,
                    request,
                    Notification.NotificationType.REQUEST_ACCEPTED,
                    "Request Accepted",
                    "Your request has been accepted and forwarded to admin."
            );

            List<User> admins = userRepository.findByRoleAndIsActive(User.Role.ADMIN, true);
            for (User admin : admins) {
                notificationService.createNotification(
                        admin,
                        employee,
                        request,
                        Notification.NotificationType.REQUEST_ACCEPTED,
                        "Approved " + request.getRequestType() + " Request",
                        "A " + request.getRequestType().name().toLowerCase() +
                                " request has been approved and needs admin review"
                );
            }

        } else if ("REJECTED".equals(action)) {
            request.setStatus(Request.Status.REJECTED);
            request.setRejectionReason(rejectionReason);

            notificationService.createNotification(
                    request.getStudent(),
                    employee,
                    request,
                    Notification.NotificationType.REQUEST_REJECTED,
                    "Request Rejected",
                    "Your request has been rejected. Reason: " + rejectionReason
            );
        }

        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public List<Request> getAcceptedRequestsForAdmin() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(Request.Status.ACCEPTED);
    }

    public Request markRequestAsPrinted(String requestId, String adminId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != Request.Status.ACCEPTED) {
            throw new RuntimeException("Request is not in accepted status");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Request.AdminReview adminReview = new Request.AdminReview();
        adminReview.setReviewedBy(adminId);
        adminReview.setReviewedAt(LocalDateTime.now());
        adminReview.setPrinted(true);
        adminReview.setPrintedAt(LocalDateTime.now());

        request.setAdminReview(adminReview);
        request.setStatus(Request.Status.COMPLETED);
        request.setUpdatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);

        notificationService.createNotification(
                request.getStudent(),
                admin,
                savedRequest,
                Notification.NotificationType.REQUEST_COMPLETED,
                "Request Completed",
                "Your request has been processed and completed."
        );

        return savedRequest;
    }

    public void deleteRequest(String requestId, String studentId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("Access denied");
        }

        if (request.getStatus() != Request.Status.DRAFT) {
            throw new RuntimeException("Cannot delete submitted request");
        }

        requestRepository.delete(request);
    }

    public List<Request> getCompletedRequests() {
        return requestRepository.findByStatusOrderByCreatedAtDesc(Request.Status.COMPLETED);
    }

    public List<Request> getRequestsReviewedByEmployee(String employeeId) {
        return requestRepository.findByEmployeeReviewReviewedBy(employeeId);
    }
}
