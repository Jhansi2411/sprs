package com.example.sprs.repository;

import com.example.sprs.model.Request;
import com.example.sprs.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends MongoRepository<Request, String> {

    List<Request> findByStudent(User student);

    List<Request> findByStudentAndStatus(User student, Request.Status status);

    List<Request> findByStatus(Request.Status status);

    List<Request> findByStatusOrderByCreatedAtDesc(Request.Status status);

    List<Request> findByRequestType(Request.RequestType requestType);

    List<Request> findByEmployeeReviewReviewedBy(String employeeId);

    List<Request> findByAdminReviewReviewedBy(String adminId);

    long countByStatus(Request.Status status);

    long countByRequestType(Request.RequestType requestType);

    long countByStudentAndStatus(User student, Request.Status status);
}