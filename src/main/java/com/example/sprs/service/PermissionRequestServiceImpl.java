package com.example.sprs.service;

import com.example.sprs.dto.StatusUpdateRequest;
import com.example.sprs.model.PermissionRequest;
import com.example.sprs.repository.PermissionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionRequestServiceImpl implements PermissionRequestService {

    @Autowired
    private PermissionRequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Override
    public PermissionRequest createRequest(PermissionRequest request) {
        String userId = userService.getCurrentUser().getId();
        request.setUserId(userId);
        request.setStatus(PermissionRequest.RequestStatus.DRAFT);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public Optional<PermissionRequest> findById(String id) {
        return requestRepository.findById(id);
    }

    @Override
    public PermissionRequest updateRequest(String id, PermissionRequest request) {
        PermissionRequest existingRequest = findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Only allow updates if request is in DRAFT status
        if (existingRequest.getStatus() != PermissionRequest.RequestStatus.DRAFT) {
            throw new RuntimeException("Cannot update submitted request");
        }

        // Update fields
        existingRequest.setName(request.getName());
        existingRequest.setRollNo(request.getRollNo());
        existingRequest.setBranch(request.getBranch());
        existingRequest.setSection(request.getSection());
        existingRequest.setReason(request.getReason());
        existingRequest.setDate(request.getDate());
        existingRequest.setTime(request.getTime());
        existingRequest.setType(request.getType());
        existingRequest.setGeneratedLetter(request.getGeneratedLetter());
        existingRequest.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(existingRequest);
    }

    @Override
    public PermissionRequest generateLetter(String id) {
        PermissionRequest request = findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String letter = createLetterContent(request);
        request.setGeneratedLetter(letter);
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public PermissionRequest submitRequest(String id) {
        PermissionRequest request = findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != PermissionRequest.RequestStatus.DRAFT) {
            throw new RuntimeException("Request is already submitted");
        }

        request.setStatus(PermissionRequest.RequestStatus.SUBMITTED);
        request.setSubmittedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public List<PermissionRequest> getUserRequests(String userId) {
        return requestRepository.findByUserId(userId);
    }

    @Override
    public List<PermissionRequest> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<PermissionRequest> getInboxRequests() {
        return requestRepository.findByStatusOrderBySubmittedAtDesc(PermissionRequest.RequestStatus.SUBMITTED);
    }

    @Override
    public PermissionRequest updateStatus(String id, StatusUpdateRequest statusUpdate) {
        PermissionRequest request = findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(statusUpdate.getStatus());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(statusUpdate.getReviewedBy());
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public void deleteRequest(String id) {
        PermissionRequest request = findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Only allow deletion if request is in DRAFT status
        if (request.getStatus() != PermissionRequest.RequestStatus.DRAFT) {
            throw new RuntimeException("Cannot delete submitted request");
        }

        requestRepository.deleteById(id);
    }

    private String createLetterContent(PermissionRequest request) {
        String typeLabel = getTypeLabel(request.getType());
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return String.format("""
                PERMISSION REQUEST LETTER
                
                Date: %s
                
                To,
                The Head of Department
                Department of %s
                [College Name]
                
                Subject: Request for %s
                
                Respected Sir/Madam,
                
                I am %s, a student of %s department, Section %s, bearing Roll Number %s.
                
                I am writing to request permission for %s on %s at %s.
                
                Reason: %s
                
                I assure you that I will maintain the discipline and reputation of the institution during this period. I will also ensure that my academic activities are not affected by this absence.
                
                I kindly request you to grant me permission for the same.
                
                Thank you for your consideration.
                
                Yours sincerely,
                %s
                Roll No: %s
                Section: %s
                """,
                currentDate,
                request.getBranch(),
                typeLabel,
                request.getName(),
                request.getBranch(),
                request.getSection(),
                request.getRollNo(),
                typeLabel.toLowerCase(),
                request.getDate(),
                request.getTime(),
                request.getReason(),
                request.getName(),
                request.getRollNo(),
                request.getSection()
        );
    }

    private String getTypeLabel(PermissionRequest.RequestType type) {
        return switch (type) {
            case OUTPASS -> "Outpass";
            case EVENTS -> "Event Participation";
            case FEE -> "Fee Related";
            case OTHERS -> "General Permission";
        };
    }
}