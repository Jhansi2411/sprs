package com.example.sprs.controller;

import com.example.sprs.model.PermissionRequest;
import com.example.sprs.model.PermissionRequest.RequestStatus;
import com.example.sprs.repository.PermissionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class PermissionRequestController {

    @Autowired
    private PermissionRequestRepository repository;

    // ➕ Create a new DRAFT request
    @PostMapping
    public PermissionRequest createRequest(@RequestBody PermissionRequest request) {
        request.setStatus(RequestStatus.DRAFT);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return repository.save(request);
    }

    // 📝 Update the draft request
    @PutMapping("/{id}")
    public ResponseEntity<PermissionRequest> updateRequest(@PathVariable String id,
                                                           @RequestBody PermissionRequest updatedRequest) {
        return repository.findById(id).map(existing -> {
            existing.setStudentName(updatedRequest.getStudentName());
            existing.setStudentId(updatedRequest.getStudentId());
            existing.setRollNo(updatedRequest.getRollNo());
            existing.setBranch(updatedRequest.getBranch());
            existing.setSection(updatedRequest.getSection());
            existing.setReason(updatedRequest.getReason());
            existing.setDate(updatedRequest.getDate());
            existing.setTime(updatedRequest.getTime());
            existing.setType(updatedRequest.getType());
            existing.setLetterContent(updatedRequest.getLetterContent());
            existing.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🚀 Submit the request (changes status to SUBMITTED and generates letter)
    @PostMapping("/{id}/submit")
    public ResponseEntity<PermissionRequest> submitRequest(@PathVariable String id) {
        return repository.findById(id).map(req -> {
            req.setStatus(RequestStatus.SUBMITTED);
            req.setUpdatedAt(LocalDateTime.now());

            // Optional: Auto-generate letter content
            String letter = String.format("This is to request %s permission for %s at %s.\n\nReason: %s\n\nSincerely,\n%s (%s)",
                    req.getType(), req.getDate(), req.getTime(), req.getReason(), req.getStudentName(), req.getRollNo());
            req.setLetterContent(letter);

            return ResponseEntity.ok(repository.save(req));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Approve request (HOD)
    @PostMapping("/{id}/approve")
    public ResponseEntity<PermissionRequest> approveRequest(@PathVariable String id) {
        return repository.findById(id).map(req -> {
            req.setStatus(RequestStatus.APPROVED);
            req.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(repository.save(req));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ❌ Reject request (HOD)
    @PostMapping("/{id}/reject")
    public ResponseEntity<PermissionRequest> rejectRequest(@PathVariable String id) {
        return repository.findById(id).map(req -> {
            req.setStatus(RequestStatus.REJECTED);
            req.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(repository.save(req));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔍 Get request by ID
    @GetMapping("/{id}")
    public ResponseEntity<PermissionRequest> getRequestById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 📋 Get all requests (HOD)
    @GetMapping
    public List<PermissionRequest> getAllRequests() {
        return repository.findAll();
    }

    // 👨‍🎓 Get all requests for a student
    @GetMapping("/student/{studentId}")
    public List<PermissionRequest> getRequestsByStudent(@PathVariable String studentId) {
        return repository.findByStudentId(studentId);
    }

    // 🗑️ Delete request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
