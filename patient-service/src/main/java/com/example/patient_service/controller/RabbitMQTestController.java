package com.example.patient_service.controller;

import com.example.patient_service.rabbitmq.RabbitMQProducer;
import com.example.patient_service.rabbitmq.PatientMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// this controller is used to send PatientMessage objects to RabbitMQ for testing purposes
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMQTestController {

    private final RabbitMQProducer rabbitMQProducer;

    @Autowired
    public RabbitMQTestController(RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody PatientMessage message) {
        rabbitMQProducer.sendMessage(message);
        return "PatientMessage sent to RabbitMQ: " + message;
    }
}
