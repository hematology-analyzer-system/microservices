package com.example.patient_service.rabbitmq;

import com.example.patient_service.config.RabbitMQConfig;
import com.example.patient_service.model.PatientRabbitMQEvent;
import com.example.patient_service.model.Patient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PatientRabbitMQProducer {
    // Attribute rabbitTemplate manages all tasks in sending request (notification)
    private final RabbitTemplate rabbitTemplate;

    // Constructor DI
    public PatientRabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Publish Add-patient-event to RabbitMQ
    public void sendAddPatientEvent(Patient myPatient) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_ADD_ROUTING_KEY,
                PatientRabbitMQEvent.builder()
                        .eventID(UUID.randomUUID().toString())
                        .eventTimestamp(LocalDateTime.now())
                        .action("ADD_PATIENT")
                        .payload(myPatient)
                        .build());
        System.out.println("Sent Add-patient-event successfully:\n" + myPatient);
    }

    // Publish Update-patient-event to RabbitMQ
    public void sendUpdatePatientEvent(Patient myPatient) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_UPDATE_ROUTING_KEY,
                PatientRabbitMQEvent.builder()
                        .eventID(UUID.randomUUID().toString())
                        .eventTimestamp(LocalDateTime.now())
                        .action("UPDATE_PATIENT")
                        .payload(myPatient)
                        .build());
        System.out.println("Sent Update-patient-event successfully:\n" + myPatient);
    }

    // Publish Delete-patient-event to RabbitMQ
    public void sendDeletePatientEvent(Patient myPatient) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PATIENT_EXCHANGE,
                RabbitMQConfig.PATIENT_DELETE_ROUTING_KEY,
                PatientRabbitMQEvent.builder()
                        .eventID(UUID.randomUUID().toString())
                        .eventTimestamp(LocalDateTime.now())
                        .action("DELETE_PATIENT")
                        .payload(myPatient)
                        .build());
        System.out.println("Sent Delete-patient-event successfully:\n" + myPatient);
    }
}
