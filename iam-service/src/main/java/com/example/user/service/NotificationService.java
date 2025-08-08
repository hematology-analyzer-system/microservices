package com.example.user.service;

import com.example.user.model.UserAuditLog;
import com.example.user.repository.UserAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final UserAuditLogRepository userAuditLogRepository;

    public NotificationService(UserAuditLogRepository userAuditLogRepository) {
        this.userAuditLogRepository = userAuditLogRepository;
    }

    // Actions that should be shown as notifications
    private static final List<String> NOTIFICATION_ACTIONS = Arrays.asList(
        "LOCK_USER", "UNLOCK_USER", "DELETE_USER", "USER_CREATE"
    );

    /**
     * Get paginated notifications for the current user based on their role privileges
     */
    public Page<UserAuditLog> getNotificationsForCurrentUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        // Find audit logs for notification-worthy actions
        Page<UserAuditLog> result = userAuditLogRepository.findByActionIn(NOTIFICATION_ACTIONS, pageable);
        log.info("Fetched {} notifications out of {} total for page {}", 
                result.getNumberOfElements(), result.getTotalElements(), page);
        return result;
    }

    /**
     * Get unread notification count for the current user
     */
    public long getUnreadCountForCurrentUser() {
        // For now, return count of all notification-worthy actions
        // In a real system, you'd track read status per user
        long count = userAuditLogRepository.countByActionIn(NOTIFICATION_ACTIONS);
        log.info("Unread notification count: {} for actions: {}", count, NOTIFICATION_ACTIONS);
        return count;
    }

    /**
     * Mark a specific notification as read (placeholder implementation)
     */
    public void markAsRead(String id) {
        // Placeholder - in a real system, you'd maintain read status per user
        log.info("Marking notification {} as read for current user", id);
    }

    /**
     * Mark all notifications as read for the current user (placeholder implementation)
     */
    public void markAllAsReadForCurrentUser() {
        // Placeholder - in a real system, you'd maintain read status per user
        log.info("Marking all notifications as read for current user");
    }

    /**
     * Get all notifications (admin endpoint)
     */
    public Page<UserAuditLog> getAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return userAuditLogRepository.findByActionIn(NOTIFICATION_ACTIONS, pageable);
    }
}
