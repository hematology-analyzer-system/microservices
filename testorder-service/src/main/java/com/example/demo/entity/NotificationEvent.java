package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Set;

/**
 * Entity to store notification events in MongoDB
 * Compatible with patient-service NotificationEvent pattern
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "testorderMessage")
public class NotificationEvent {
    
    @Id
    private String id;                  // MongoDB generated ID

    private String eventId;             // Independent UUID for cross-service reference
    
    private String entityType;          // TEST_ORDER, RESULT, COMMENT
    private String entityId;            // ID of specific entity for navigation
    
    private String action;              // CREATE, UPDATE, DELETE, REVIEW, COMMENT
    
    private String title;               // Short description for toast notification
    private String message;             // Detailed message for notification center
    
    private Set<Long> targetPrivileges; // User privileges that can receive this notification
    private Boolean isGlobal;           // If true, all users receive this notification
    
    private Boolean isRead;             // Read/unread status
    
    @JsonFormat(timezone = "UTC")
    @Field("createdAt")
    private Instant createdAt;          // When the event was created
    
    private String createdBy;           // Username of the user who triggered the event
    
    private Object data;                // Main payload (TestOrder, Result, Comment object)
    
    // Additional fields specific to TestOrder domain
    private String orderStatus;        // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority;           // LOW, NORMAL, HIGH, URGENT
    private String testType;           // Blood Test type
}