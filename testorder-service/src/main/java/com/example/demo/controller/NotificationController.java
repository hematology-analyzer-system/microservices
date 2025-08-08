package com.example.demo.controller;

import com.example.demo.entity.NotificationEvent;
import com.example.demo.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing notifications
 * Provides endpoints for retrieving and managing user notifications
 */
@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Get all notifications for current user
     * GET /testorder/notifications
     */
    @GetMapping
    public ResponseEntity<List<NotificationEvent>> getNotifications() {
        try {
            List<NotificationEvent> notifications = notificationService.getAllNotificationsOfCurrentUser();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error retrieving notifications: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get paginated notifications for current user
     * GET /testorder/notifications/paging?page=0&size=10
     */
    @GetMapping("/paging")
    public ResponseEntity<Page<NotificationEvent>> getPagingNotifications(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size) {
        try {
            Page<NotificationEvent> notifications = notificationService
                    .getPagingNotificationOfCurrentUser(page, size, "createdAt");
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error retrieving paginated notifications: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get count of unread notifications for current user
     * GET /testorder/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        try {
            long count = notificationService.countUnreadNotificationsOfCurrentUser();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error counting unread notifications: {}", e.getMessage());
            return ResponseEntity.ok(0L);
        }
    }

    /**
     * Mark a specific notification as read
     * PUT /testorder/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Mark all notifications as read for current user
     * PUT /testorder/notifications/mark-all-read
     */
    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking all notifications as read: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get notifications for a specific entity (test order, result, etc.)
     * GET /testorder/notifications/entity/{entityType}/{entityId}
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<NotificationEvent>> getNotificationsByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        try {
            List<NotificationEvent> notifications = notificationService
                    .getNotificationsByEntity(entityType, entityId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error retrieving entity notifications: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint for notifications
     * GET /testorder/notifications/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notifications service is healthy");
    }
}