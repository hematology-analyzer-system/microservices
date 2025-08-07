package com.example.patient_service.rabbitmq;

import com.example.patient_service.config.RabbitMQConfig;
import com.example.patient_service.model.NotificationEvent;
import com.example.patient_service.repository.PatientMongoRepository;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/*
 * Consumer has 3 tasks:
 * 1. Listen to queues of RabbitMQ and receive/consume event
 * 2. SAVE event to MongoDB (to store history)
 * 3. PUSH event to Websocket (to send realtime notification)
 */
@Service
public class PatientRabbitMQConsumer {
    // Attribute represents for MongoDB
    @Autowired
    PatientMongoRepository patientMongoRepository;

    // Attribute represent for STOMP
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // Consumer will "listen" to 3 queues, take the message in these queses and save to mongoDB
    @RabbitListener(
            queues = {RabbitMQConfig.PATIENT_ADD_QUEUE, RabbitMQConfig.PATIENT_UPDATE_QUEUE, RabbitMQConfig.PATIENT_DELETE_QUEUE},
            ackMode = "MANUAL")
    public void handlePatientEvent(NotificationEvent myEvent, Message message, Channel channel) throws Exception {
        try {
            // Save patient-event to mongoDB
            patientMongoRepository.save(myEvent);

            // Push patient-event to websocket
            simpMessagingTemplate.convertAndSend("/topic/notification", myEvent);

            // Send ACK to remove event from PATIENT_QUEUE
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("Save Patient-event & push to WS & ACK to RabbitMQ successfully");

        } catch (Exception e) {
            // Send NACK to redelivery event to PATIENT_QUEUE
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            System.out.println("An exception occurred while processing patient event: " + e.getMessage());
        }
    }
}
