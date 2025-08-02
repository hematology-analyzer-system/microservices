// package com.example.user.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// // this controller is used to send requests to RabbitMQ for testing purposes
// @RestController
// @RequestMapping("/rabbitmq")
// public class RabbitMQRequestController {

//     private final RabbitTemplate rabbitTemplate;

//     @Autowired
//     public RabbitMQRequestController(RabbitTemplate rabbitTemplate) {
//         this.rabbitTemplate = rabbitTemplate;
//     }

//     @PostMapping("/sendRequest")
//     public String sendRequest(@RequestBody String message) {
//         rabbitTemplate.convertAndSend("appExchange", "request.key", message);
//         return "Request sent to appExchange with routing key 'request.key': " + message;
//     }
// }
