package com.example.demo.rabbitmq;

import com.example.demo.entity.NotificationEvent;
import com.example.demo.service.NotificationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ Consumer for TestOrder notifications
 * Handles notification events and pushes them to WebSocket subscribers
 * Based on patient-service pattern
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestOrderNotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Consumer for TestOrder notification events
     * Handles 3 main tasks:
     * 1. Save notification to MongoDB
     * 2. Push notification to WebSocket subscribers
     * 3. ACK/NACK the message
     */
    @RabbitListener(queues = { "testorder.notification.created.q", "testorder.notification.updated.q",
            "testorder.notification.status.q", "testorder.notification.result.q" }, ackMode = "MANUAL")
    public void handleTestOrderNotification(NotificationEvent notificationEvent, Message message, Channel channel) {
        try {
            log.info("Received TestOrder notification: eventId={}, action={}, entityId={}",
                    notificationEvent.getEventId(), notificationEvent.getAction(), notificationEvent.getEntityId());

            // 1. Save notification to MongoDB
            notificationService.saveNotification(notificationEvent);
            log.debug("Saved notification to MongoDB: {}", notificationEvent.getEventId());

            // 2. Push notification to WebSocket based on type
            pushToWebSocket(notificationEvent);
            log.debug("Pushed notification to WebSocket: {}", notificationEvent.getEventId());

            // 3. Send ACK to remove message from queue
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("Successfully processed TestOrder notification: eventId={}", notificationEvent.getEventId());

        } catch (Exception e) {
            try {
                // Send NACK to requeue message for retry
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.error("Failed to process TestOrder notification, message requeued: eventId={}, error={}",
                        notificationEvent.getEventId(), e.getMessage(), e);
            } catch (Exception ackException) {
                log.error("Failed to NACK message: {}", ackException.getMessage(), ackException);
            }
        }
    }

    /**
     * Push notifications to appropriate WebSocket topics
     */
    private void pushToWebSocket(NotificationEvent notificationEvent) {
        try {
            String action = notificationEvent.getAction();

            // General testorder notifications topic
            simpMessagingTemplate.convertAndSend("/topic/testorder", notificationEvent);

            // Specific action-based topics for targeted subscriptions
            switch (action) {
                case "CREATE":
                    simpMessagingTemplate.convertAndSend("/topic/testorder/created", notificationEvent);
                    break;
                case "UPDATE":
                    simpMessagingTemplate.convertAndSend("/topic/testorder/updated", notificationEvent);
                    break;
                case "STATUS_CHANGE":
                    simpMessagingTemplate.convertAndSend("/topic/testorder/status", notificationEvent);
                    break;
                case "RESULT_AVAILABLE":
                    simpMessagingTemplate.convertAndSend("/topic/testorder/results", notificationEvent);
                    break;
                default:
                    log.warn("Unknown action type for WebSocket routing: {}", action);
            }

            // Priority-based topics for urgent notifications
            if ("URGENT".equalsIgnoreCase(notificationEvent.getPriority())) {
                simpMessagingTemplate.convertAndSend("/topic/testorder/urgent", notificationEvent);
            }

            log.debug("Successfully pushed notification to WebSocket topics for action: {}", action);

        } catch (Exception e) {
            log.error("Failed to push notification to WebSocket: eventId={}, error={}",
                    notificationEvent.getEventId(), e.getMessage(), e);
            // Don't throw exception here as we still want to ACK the message
            // WebSocket failure shouldn't cause message reprocessing
        }
    }

    /**
     * Consumer for patient events that might affect test orders
     * This allows cross-service notification when patient data changes
     */
    @RabbitListener(queues = { "testorder.patient.created.q", "testorder.patient.updated.q" }, ackMode = "MANUAL")
    public void handlePatientNotification(Object patientEvent, Message message, Channel channel) {
        try {
            log.info("Received patient notification that might affect test orders");

            // Here you could implement logic to:
            // 1. Check if patient has active test orders
            // 2. Create notifications for affected test orders
            // 3. Push updates to WebSocket

            // For now, just log and acknowledge
            log.debug("Processed patient notification - implementation needed for business logic");

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.error("Failed to process patient notification: {}", e.getMessage(), e);
            } catch (Exception ackException) {
                log.error("Failed to NACK patient notification: {}", ackException.getMessage(), ackException);
            }
        }
    }
}