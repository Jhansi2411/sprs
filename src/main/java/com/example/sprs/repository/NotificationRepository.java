package com.example.sprs.repository;

import com.example.sprs.model.Notification;
import com.example.sprs.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    Page<Notification> findByRecipientAndIsReadOrderByCreatedAtDesc(User recipient, boolean isRead, Pageable pageable);

    List<Notification> findByRecipientAndIsReadOrderByCreatedAtDesc(User recipient, boolean isRead);

    long countByRecipientAndIsRead(User recipient, boolean isRead);

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndIsReadTrueAndCreatedAtBefore(User recipient, LocalDateTime createdAt);

    void deleteByRecipientAndIsRead(User recipient, boolean isRead);
}