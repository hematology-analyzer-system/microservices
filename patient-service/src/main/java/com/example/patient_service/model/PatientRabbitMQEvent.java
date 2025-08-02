package com.example.patient_service.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class PatientRabbitMQEvent {
    @Id
    private String id; // Automated identify number for patient event
    private String action; // ADD_PATIENT, UPDATE_PATIENT, DELETE_PATIENT
    private Object payload; // Content of patient information
}
