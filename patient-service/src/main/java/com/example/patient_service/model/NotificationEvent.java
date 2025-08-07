package com.example.patient_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "patientMessage")
public class NotificationEvent {
    @Id
    private String id;                  // Dependent with specific database (will be changed when db changes - instable)

    private String eventId;             // Independent with specific database (still well-working even when changes in db occurs)

    private String entityType;          // PATIENT, TEST_ORDER, IAM (USER/ROLE)
    private String entityId;            // ID of specific entity (patient/test order...) => easy to navigate to see details

    private String action;              // ADD, UPDATE, DELETE

    private String title;               // For general information (toast)
    private String message;             // For detail information (see detail)

    private Set<Long> targetPrivileges; // Only user with exact privileges can receive event
    private Boolean isGlobal;           // True --> all users can receive event

    private Boolean isRead;             // Mark Read/UnRead

    @JsonFormat(timezone = "UTC")
    @Field("createdAt")
    private Instant createdAt;          // The exact time when the event is created
    private String createBy;            // Store information of Actor who causes/creates this event

    private Object data;                // Main payload
}