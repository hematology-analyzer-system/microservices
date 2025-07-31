package com.example.patient_service.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@Service
public class RabbitMQTwoWayConsumer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQTwoWayConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Listen for requests, process, and send response
    @RabbitListener(queues = "requestQueue", ackMode = "MANUAL")
    public void receiveRequest(Message message, Channel channel) throws IOException {
        String request = new String(message.getBody());
        System.out.println("[PatientService] Received request: " + request);
        String response = "[PatientService] Processed: " + request;
        rabbitTemplate.convertAndSend("appExchange", "response.key", response);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    // Listen for responses
    @RabbitListener(queues = "responseQueue", ackMode = "MANUAL")
    public void receiveResponse(Message message, Channel channel) throws IOException {
        String response = new String(message.getBody());
        System.out.println("[PatientService] Received response: " + response);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
