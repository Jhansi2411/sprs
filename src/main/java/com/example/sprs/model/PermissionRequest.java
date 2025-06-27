package com.example.sprs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "requests")
public class PermissionRequest {

    @Id
    private String id;

    // 🧑 Student Info
    private String studentId;    // Optional: link to User._id
    private String studentName;
    private String rollNo;
    private String branch;
    private String section;

    // 📄 Request Info
    private RequestType type;    // OUTPASS, EVENT, FEE, OTHER
    private String reason;
    private String date;
    private String time;
    private String letterContent;

    // 🚦 Status Info
    private RequestStatus status;  // DRAFT, SUBMITTED, APPROVED, REJECTED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ——— Enums ———
    public enum RequestType {
        OUTPASS, EVENT, FEE, OTHER
    }

    public enum RequestStatus {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }

    // ——— Getters and Setters ———

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLetterContent() { return letterContent; }
    public void setLetterContent(String letterContent) { this.letterContent = letterContent; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
