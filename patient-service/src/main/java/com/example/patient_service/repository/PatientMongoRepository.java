package com.example.patient_service.repository;

import com.example.patient_service.model.PatientRabbitMQEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientMongoRepository extends MongoRepository<PatientRabbitMQEvent, String> {
}
