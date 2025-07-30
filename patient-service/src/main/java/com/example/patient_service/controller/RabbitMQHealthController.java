package com.example.patient_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// this file is used to test the functionality of RabbitMQ
// it checks if the RabbitMQ controller is active
@RestController
@RequestMapping("/rabbitmq")
public class RabbitMQHealthController {

    @GetMapping("/health")
    public String health() {
        return "RabbitMQ Controller is active";
    }
}
