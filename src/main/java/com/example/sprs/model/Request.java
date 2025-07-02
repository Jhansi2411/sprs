package com.example.sprs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "requests")
public class Request {
    @Id
    private String id;

    @DBRef
    private User student;

    private RequestType requestType;

    private FormData formData;

    private String generatedLetter;

    private Status status = Status.DRAFT;

    private EmployeeReview employeeReview;

    private AdminReview adminReview;

    private String rejectionReason;

    private Priority priority = Priority.MEDIUM;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Request() {}

    public Request(User student, RequestType requestType, FormData formData) {
        this.student = student;
        this.requestType = requestType;
        this.formData = formData;
    }

    // Method to generate letter
    public void generateLetter() {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        StringBuilder letter = new StringBuilder();
        letter.append("Date: ").append(currentDate).append("\n\n");
        letter.append("To,\n");
        letter.append("The Head of Department,\n");
        letter.append(formData.getBranch()).append(" Department\n\n");
        letter.append("Subject: Request for ").append(requestType.name()).append("\n\n");
        letter.append("Respected Sir/Madam,\n\n");
        letter.append("I am ").append(formData.getName());
        letter.append(", a student of ").append(formData.getBranch());
        letter.append(" department, Section ").append(formData.getSection());
        letter.append(", with Roll Number ").append(formData.getRollNo()).append(".\n\n");
        letter.append("I am writing to request permission for ");
        letter.append(requestType.name().toLowerCase()).append(" on ");
        letter.append(formData.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        letter.append(" at ").append(formData.getTime()).append(".\n\n");
        letter.append("Reason: ").append(formData.getReason()).append("\n\n");
        letter.append("I assure you that I will maintain discipline and follow all the guidelines. ");
        letter.append("I request you to kindly grant me permission for the same.\n\n");
        letter.append("Contact Number: ").append(formData.getContact()).append("\n\n");
        letter.append("Thanking you,\n\n");
        letter.append("Yours sincerely,\n");
        letter.append(formData.getName()).append("\n");
        letter.append("Roll No: ").append(formData.getRollNo()).append("\n");
        letter.append("Section: ").append(formData.getSection());

        this.generatedLetter = letter.toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }

    public FormData getFormData() { return formData; }
    public void setFormData(FormData formData) { this.formData = formData; }

    public String getGeneratedLetter() { return generatedLetter; }
    public void setGeneratedLetter(String generatedLetter) { this.generatedLetter = generatedLetter; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public EmployeeReview getEmployeeReview() { return employeeReview; }
    public void setEmployeeReview(EmployeeReview employeeReview) { this.employeeReview = employeeReview; }

    public AdminReview getAdminReview() { return adminReview; }
    public void setAdminReview(AdminReview adminReview) { this.adminReview = adminReview; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Enums
    public enum RequestType {
        OUTING, EVENTS, FEE, OTHERS
    }

    public enum Status {
        DRAFT, PENDING, ACCEPTED, REJECTED, COMPLETED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    // Nested Classes
    public static class FormData {
        private String name;
        private String rollNo;
        private String branch;
        private String section;
        private LocalDateTime date;
        private String time;
        private String contact;
        private String reason;

        // Constructors
        public FormData() {}

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getRollNo() { return rollNo; }
        public void setRollNo(String rollNo) { this.rollNo = rollNo; }

        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }

        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }

        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class EmployeeReview {
        private String reviewedBy;
        private LocalDateTime reviewedAt;
        private String comments;
        private String action;

        // Constructors
        public EmployeeReview() {}

        public EmployeeReview(String reviewedBy, String comments, String action) {
            this.reviewedBy = reviewedBy;
            this.comments = comments;
            this.action = action;
            this.reviewedAt = LocalDateTime.now();
        }

        // Getters and Setters
        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

        public LocalDateTime getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }

    public static class AdminReview {
        private String reviewedBy;
        private LocalDateTime reviewedAt;
        private String comments;
        private boolean printed = false;
        private LocalDateTime printedAt;

        // Constructors
        public AdminReview() {}

        // Getters and Setters
        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

        public LocalDateTime getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }

        public boolean isPrinted() { return printed; }
        public void setPrinted(boolean printed) { this.printed = printed; }

        public LocalDateTime getPrintedAt() { return printedAt; }
        public void setPrintedAt(LocalDateTime printedAt) { this.printedAt = printedAt; }
    }
}