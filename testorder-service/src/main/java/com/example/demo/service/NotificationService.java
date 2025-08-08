package com.example.demo.service;

import com.example.demo.entity.NotificationEvent;
import com.example.demo.repository.NotificationMongoRepository;
import com.example.demo.security.CurrentUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing notifications with privilege-based filtering
 * Based on patient-service pattern
 */
@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationMongoRepository notificationMongoRepository;

    /**
     * Get all notifications for current user based on their privileges
     */
    public List<NotificationEvent> getAllNotificationsOfCurrentUser() {
        try {
            CurrentUser currentUser = getCurrentUser();
            Set<Long> userPrivileges = currentUser.getPrivileges();

            return notificationMongoRepository.findAllByUserPrivileges(userPrivileges).stream()
                    .sorted(Comparator.comparing(NotificationEvent::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Exception in NotificationService.getAllNotificationsOfCurrentUser: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve notifications: " + e.getMessage());
        }
    }

    /**
     * Get paginated notifications for current user
     */
    public Page<NotificationEvent> getPagingNotificationOfCurrentUser(Integer page, Integer size, String sortBy) {
        try {
            CurrentUser currentUser = getCurrentUser();
            Set<Long> userPrivileges = currentUser.getPrivileges();
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
            return notificationMongoRepository.findByUserPrivileges(userPrivileges, pageable);
        } catch (Exception e) {
            log.error("Exception in NotificationService.getPagingNotificationOfCurrentUser: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve paginated notifications: " + e.getMessage());
        }
    }

    /**
     * Count unread notifications for current user
     */
    public long countUnreadNotificationsOfCurrentUser() {
        try {
            CurrentUser currentUser = getCurrentUser();
            Set<Long> userPrivileges = currentUser.getPrivileges();
            return notificationMongoRepository.countUnreadByUserPrivileges(userPrivileges);
        } catch (Exception e) {
            log.error("Exception in NotificationService.countUnreadNotificationsOfCurrentUser: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Mark a notification as read
     */
    public void markAsRead(String notificationId) {
        try {
            Optional<NotificationEvent> eventOpt = notificationMongoRepository.findById(notificationId);
            if (eventOpt.isPresent()) {
                NotificationEvent event = eventOpt.get();
                event.setIsRead(true);
                notificationMongoRepository.save(event);
                log.info("Marked notification {} as read", notificationId);
            } else {
                log.warn("Notification with ID {} not found", notificationId);
                throw new RuntimeException("Notification not found");
            }
        } catch (Exception e) {
            log.error("Exception in NotificationService.markAsRead: {}", e.getMessage());
            throw new RuntimeException("Failed to mark notification as read: " + e.getMessage());
        }
    }

    /**
     * Mark all notifications as read for current user
     */
    public void markAllAsRead() {
        try {
            List<NotificationEvent> events = getAllNotificationsOfCurrentUser();
            events.forEach(event -> {
                if (!event.getIsRead()) {
                    event.setIsRead(true);
                    notificationMongoRepository.save(event);
                }
            });
            log.info("Marked {} notifications as read for current user", events.size());
        } catch (Exception e) {
            log.error("Exception in NotificationService.markAllAsRead: {}", e.getMessage());
            throw new RuntimeException("Failed to mark all notifications as read: " + e.getMessage());
        }
    }

    /**
     * Get notifications by entity type and ID (for specific test order, result,
     * etc.)
     */
    public List<NotificationEvent> getNotificationsByEntity(String entityType, String entityId) {
        try {
            // Filter by user privileges as well
            CurrentUser currentUser = getCurrentUser();
            Set<Long> userPrivileges = currentUser.getPrivileges();

            return notificationMongoRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                    .stream()
                    .filter(event -> event.getIsGlobal() ||
                            (event.getTargetPrivileges() != null &&
                                    event.getTargetPrivileges().stream().anyMatch(userPrivileges::contains)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Exception in NotificationService.getNotificationsByEntity: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve entity notifications: " + e.getMessage());
        }
    }

    /**
     * Save a notification (used by consumers)
     */
    public NotificationEvent saveNotification(NotificationEvent notification) {
        try {
            return notificationMongoRepository.save(notification);
        } catch (Exception e) {
            log.error("Exception in NotificationService.saveNotification: {}", e.getMessage());
            throw new RuntimeException("Failed to save notification: " + e.getMessage());
        }
    }

    /**
     * Get current authenticated user
     */
    private CurrentUser getCurrentUser() {
        try {
            return (CurrentUser) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getDetails();
        } catch (Exception e) {
            log.error("Failed to get current user: {}", e.getMessage());
            throw new RuntimeException("Failed to get current user details");
        }
    }
}