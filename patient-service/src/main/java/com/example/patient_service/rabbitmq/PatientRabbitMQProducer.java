package com.example.patient_service.rabbitmq;

import com.example.patient_service.config.RabbitMQConfig;
import com.example.patient_service.model.NotificationEvent;
import com.example.patient_service.model.Patient;
import com.example.patient_service.service.EncryptionService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class PatientRabbitMQProducer {
    // Attribute rabbitTemplate manages all tasks in sending request (notification)
    private final RabbitTemplate rabbitTemplate;
    // Attribute encryptionService manages encode/decode personal information
    private final EncryptionService encryptionService;

    // Constructor DI
    public PatientRabbitMQProducer(RabbitTemplate rabbitTemplate, EncryptionService encryptionService) {
        this.rabbitTemplate = rabbitTemplate;
        this.encryptionService = encryptionService;
    }

    // Decrypt Patient method
    private Patient decryptPatientData(Patient myPatient) {
        myPatient.setFullName(encryptionService.decrypt(myPatient.getFullName()));
        myPatient.setAddress(encryptionService.decrypt(myPatient.getAddress()));
        myPatient.setEmail(encryptionService.decrypt(myPatient.getEmail()));
        myPatient.setPhone(encryptionService.decrypt(myPatient.getPhone()));
        return myPatient;
    }

    // Publish Add-patient-event to RabbitMQ
    public void sendAddPatientEvent(Patient myPatient) {
        // Decrypt patient information before sending event
        decryptPatientData(myPatient);

        this.sendPatientEvent(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_ADD_ROUTING_KEY,
                myPatient,
                "ADD",
                "New patient added",
                String.format("Patient \"%s\" has been added to the system.", myPatient.getFullName()));
    }

    // Publish Update-patient-event to RabbitMQ
    public void sendUpdatePatientEvent(Patient myPatient) {
        // Decrypt patient information before sending event
        decryptPatientData(myPatient);

        this.sendPatientEvent(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_UPDATE_ROUTING_KEY,
                myPatient,
                "UPDATE",
                "Patient record updated",
                String.format("Information of patient \"%s\" has been updated.", myPatient.getFullName()));
    }

    // Publish Delete-patient-event to RabbitMQ
    public void sendDeletePatientEvent(Patient myPatient) {
        // Decrypt patient information before sending event
        decryptPatientData(myPatient);

        this.sendPatientEvent(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_DELETE_ROUTING_KEY,
                myPatient,
                "DELETE",
                "One patient is deleted",
                String.format("Patient \"%s\" has been deleted from the system.", myPatient.getFullName()));
    }

    /* *******************************************
    Method for common using --> apply DRY principle
    ********************************************** */
    private void sendPatientEvent(
            String myExchange,
            String myRoutingKey,
            Patient myPatient,
            String action,
            String title,
            String message
    ) {
        // Set targetPrivileges
        Set<Long> targetPrivileges = new HashSet<>();
        targetPrivileges.add(1L);

        // Create event
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .entityType("PATIENT")
                .entityId(myPatient.getId().toString())     // Navigate to patient ID detail when click in notification
                .action(action)
                .title(title)
                .message(message)
                .targetPrivileges(targetPrivileges)
                .isGlobal(false)
                .isRead(false)
                .createdAt(Instant.now())
                .createBy(myPatient.getCreatedBy())
                .data(myPatient)
                .build();

        // Send event to RabbitMQ server (queues)
        rabbitTemplate.convertAndSend(myExchange, myRoutingKey, notificationEvent);
        System.out.println("Sent " + action + " patient-event successfully:\n" + myPatient);
    }
}
