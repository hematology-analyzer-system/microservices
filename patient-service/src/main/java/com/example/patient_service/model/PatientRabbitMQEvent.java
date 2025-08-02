package com.example.patient_service.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class PatientRabbitMQEvent {
    @Id
    private String id;          // Dependent with specific database (will be changed when db changes - instable)

    private String eventID;     // Independent with specific database (still well-working even when changes in db occurs)

    private LocalDateTime eventTimestamp; // The time that event was created and officially published

    private String action;      // ADD_PATIENT, UPDATE_PATIENT, DELETE_PATIENT

    private Object payload;     // Content of patient information
}
