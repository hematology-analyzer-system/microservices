package com.example.user.controller;

import com.example.user.model.UserAuditLog;
import com.example.user.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get paginated notifications for the current user based on their role privileges
     */
    @GetMapping("/paging")
    public ResponseEntity<Page<UserAuditLog>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserAuditLog> notifications = notificationService.getNotificationsForCurrentUser(page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching notifications for user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get unread notification count for the current user
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        try {
            long unreadCount = notificationService.getUnreadCountForCurrentUser();
            return ResponseEntity.ok(unreadCount);
        } catch (Exception e) {
            log.error("Error fetching unread count for user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Mark a specific notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable String id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to mark notification as read"));
        }
    }

    /**
     * Mark all notifications as read for the current user
     */
    @PutMapping("/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        try {
            notificationService.markAllAsReadForCurrentUser();
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (Exception e) {
            log.error("Error marking all notifications as read", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to mark all notifications as read"));
        }
    }

    /**
     * Get all notifications (admin endpoint - optional)
     */
    @GetMapping("/all")
    public ResponseEntity<Page<UserAuditLog>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserAuditLog> notifications = notificationService.getAllNotifications(page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching all notifications", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
