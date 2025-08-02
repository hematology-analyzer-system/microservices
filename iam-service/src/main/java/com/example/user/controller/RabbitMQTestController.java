// package com.example.user.controller;

// import com.example.user.rabbitmq.RabbitMQProducer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// // this file is used to send messages to RabbitMQ for testing purposes
// @RestController
// @RequestMapping("/rabbitmq")
// public class RabbitMQTestController {

//     private final RabbitMQProducer rabbitMQProducer;

//     @Autowired
//     public RabbitMQTestController(RabbitMQProducer rabbitMQProducer) {
//         this.rabbitMQProducer = rabbitMQProducer;
//     }

//     @PostMapping("/send")
//     public String sendMessage(@RequestBody String message) {
//         rabbitMQProducer.sendMessage(message);
//         return "Message sent to RabbitMQ: " + message;
//     }
// }
