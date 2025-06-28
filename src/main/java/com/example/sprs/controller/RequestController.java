package com.example.sprs.controller;

import com.example.sprs.dto.StatusUpdateRequest;
import com.example.sprs.model.PermissionRequest;
import com.example.sprs.service.PermissionRequestService;
import com.example.sprs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
@CrossOrigin(origins = "*")
public class RequestController {

    @Autowired
    private PermissionRequestService requestService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@Valid @RequestBody PermissionRequest request) {
        try {
            PermissionRequest createdRequest = requestService.createRequest(request);
            return ResponseEntity.ok(createdRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create request: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequest(@PathVariable String id) {
        try {
            PermissionRequest request = requestService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Request not found: " + e.getMessage());
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editRequest(@PathVariable String id, @Valid @RequestBody PermissionRequest request) {
        try {
            PermissionRequest updatedRequest = requestService.updateRequest(id, request);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update request: " + e.getMessage());
        }
    }

    @PostMapping("/generate-letter/{id}")
    public ResponseEntity<?> generateLetter(@PathVariable String id) {
        try {
            PermissionRequest request = requestService.generateLetter(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate letter: " + e.getMessage());
        }
    }

    @PostMapping("/submit/{id}")
    public ResponseEntity<?> submitRequest(@PathVariable String id) {
        try {
            PermissionRequest request = requestService.submitRequest(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit request: " + e.getMessage());
        }
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<PermissionRequest>> getMyRequests() {
        try {
            String userId = userService.getCurrentUser().getId();
            List<PermissionRequest> requests = requestService.getUserRequests(userId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<PermissionRequest>> getInboxRequests() {
        try {
            List<PermissionRequest> requests = requestService.getInboxRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PermissionRequest>> getAllRequests() {
        try {
            List<PermissionRequest> requests = requestService.getAllRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest statusUpdate) {
        try {
            PermissionRequest request = requestService.updateStatus(id, statusUpdate);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update status: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable String id) {
        try {
            requestService.deleteRequest(id);
            return ResponseEntity.ok().body("Request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete request: " + e.getMessage());
        }
    }
}