package com.example.sprs.service;

import com.example.sprs.model.Notification;
import com.example.sprs.model.Request;
import com.example.sprs.model.User;
import com.example.sprs.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(User recipient, User sender, Request request,
                                           Notification.NotificationType type, String title, String message) {
        Notification notification = new Notification(recipient, sender, request, type, title, message);

        Notification.Metadata metadata = new Notification.Metadata(
                request.getRequestType().name(),
                request.getStatus().name(),
                sender.getProfile().getName()
        );
        notification.setMetadata(metadata);

        logger.info("Creating notification for recipient: {}", recipient.getUsername());
        return notificationRepository.save(notification);
    }

    public Page<Notification> getUserNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
    }

    public Page<Notification> getUnreadNotifications(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false, pageable);
    }

    public List<Notification> getAllUserNotifications(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndIsRead(user, false);
    }

    public Optional<Notification> findById(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public Notification markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            logger.warn("Access denied for userId: {} on notificationId: {}", userId, notificationId);
            throw new RuntimeException("Access denied");
        }

        if (!notification.isRead()) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }

        return notification;
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications =
                notificationRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false);

        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }

        notificationRepository.saveAll(unreadNotifications);
        logger.info("Marked {} notifications as read for user: {}", unreadNotifications.size(), user.getUsername());
    }

    public void deleteNotification(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            logger.warn("Delete denied for userId: {} on notificationId: {}", userId, notificationId);
            throw new RuntimeException("Access denied");
        }

        notificationRepository.delete(notification);
        logger.info("Notification {} deleted for userId: {}", notificationId, userId);
    }

    public void deleteOldReadNotifications(User user, int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        List<Notification> oldReadNotifications = notificationRepository
                .findByRecipientAndIsReadTrueAndCreatedAtBefore(user, cutoff);

        notificationRepository.deleteAll(oldReadNotifications);
        logger.info("Deleted {} old read notifications for user: {}", oldReadNotifications.size(), user.getUsername());
    }
}
