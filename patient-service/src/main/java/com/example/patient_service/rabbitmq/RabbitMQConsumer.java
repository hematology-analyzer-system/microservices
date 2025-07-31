package com.example.patient_service.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
// ...existing code...

@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = "patientQueue")
    public void receiveMessage(PatientMessage message) {
        // Handle the received message
        System.out.println("Received PatientMessage: " + message);
    }
}
