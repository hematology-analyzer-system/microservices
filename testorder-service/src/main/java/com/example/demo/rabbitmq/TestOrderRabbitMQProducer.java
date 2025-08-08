package com.example.demo.rabbitmq;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.NotificationEvent;
import com.example.demo.entity.TestOrder;
import com.example.demo.messaging.envelope.MessageEnvelope;
import com.example.demo.messaging.testorder.event.TestOrderCreatedEvent;
import com.example.demo.messaging.testorder.event.TestOrderUpdatedEvent;
import com.example.demo.service.MessageLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * RabbitMQ producer for TestOrder service
 * Publishes events following the pattern from RabbitMQ.md specification
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TestOrderRabbitMQProducer {

        private final RabbitTemplate rabbitTemplate;
        private final MessageLogService messageLogService;

        private static final String SERVICE_NAME = "testorder-service";

        /**
         * Publish test order created event
         */
        public void publishTestOrderCreated(TestOrderCreatedEvent event, String correlationId) {
                try {
                        MessageEnvelope<TestOrderCreatedEvent> messageEnvelope = MessageEnvelope
                                        .<TestOrderCreatedEvent>builder()
                                        .id(UUID.randomUUID())
                                        .type(RabbitMQConfig.TESTORDER_CREATED_ROUTING_KEY)
                                        .source(SERVICE_NAME)
                                        .timestamp(Instant.now())
                                        .correlationId(correlationId != null ? correlationId
                                                        : UUID.randomUUID().toString())
                                        .payload(event)
                                        .build();

                        // Log message before sending
                        messageLogService.logOutgoingMessage(messageEnvelope,
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        RabbitMQConfig.TESTORDER_CREATED_ROUTING_KEY);

                        // Send message
                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        messageEnvelope.getType(),
                                        messageEnvelope);

                        log.info("Published TestOrder created event: testOrderId={}, correlationId={}",
                                        event.getTestOrderId(), messageEnvelope.getCorrelationId());

                } catch (Exception e) {
                        log.error("Failed to publish TestOrder created event: testOrderId={}, error={}",
                                        event.getTestOrderId(), e.getMessage(), e);
                        throw new RuntimeException("Failed to publish TestOrder created event", e);
                }
        }

        /**
         * Publish test order updated event
         */
        public void publishTestOrderUpdated(TestOrderUpdatedEvent event, String correlationId) {
                try {
                        System.out.println("event1 ");
                        MessageEnvelope<TestOrderUpdatedEvent> messageEnvelope = MessageEnvelope
                                        .<TestOrderUpdatedEvent>builder()
                                        .id(UUID.randomUUID())
                                        .type(RabbitMQConfig.TESTORDER_UPDATED_ROUTING_KEY)
                                        .source(SERVICE_NAME)
                                        .timestamp(Instant.now())
                                        .correlationId(correlationId != null ? correlationId
                                                        : UUID.randomUUID().toString())
                                        .payload(event)
                                        .build();
                        System.out.println("messageEnvelope: " + messageEnvelope);
                        // Log message before sending
                        messageLogService.logOutgoingMessage(messageEnvelope,
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        RabbitMQConfig.TESTORDER_UPDATED_ROUTING_KEY);
                        System.out.println("event 2");
                        // Send message
                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        messageEnvelope.getType(),
                                        messageEnvelope);
                        System.out.println("event 3");
                        log.info("Published TestOrder updated event: testOrderId={}, status={}, correlationId={}",
                                        event.getTestOrderId(), event.getStatus(), messageEnvelope.getCorrelationId());

                } catch (Exception e) {
                        log.error("Failed to publish TestOrder updated event: testOrderId={}, error={}",
                                        event.getTestOrderId(), e.getMessage(), e);
                        throw new RuntimeException("Failed to publish TestOrder updated event", e);
                }
        }

        // ===================== NOTIFICATION METHODS =====================

        /**
         * Publish test order created notification
         */
        public void publishTestOrderCreatedNotification(TestOrder testOrder, String createdBy) {
                try {
                        NotificationEvent notificationEvent = createNotificationEvent(
                                        testOrder,
                                        "CREATE",
                                        "New Test Order Created",
                                        String.format("Test Order #%d has been created for patient ID %d",
                                                        testOrder.getTestId(), testOrder.getPatientTOId()),
                                        createdBy);

                        // Send to notification queue
                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        "testorder.notification.created",
                                        notificationEvent);

                        log.info("Published TestOrder created notification: testOrderId={}", testOrder.getTestId());

                } catch (Exception e) {
                        log.error("Failed to publish TestOrder created notification: testOrderId={}, error={}",
                                        testOrder.getTestId(), e.getMessage(), e);
                }
        }

        /**
         * Publish test order updated notification
         */
        public void publishTestOrderUpdatedNotification(TestOrder testOrder, String updatedBy,
                        String changeDescription) {
                try {
                        String title = "Test Order Updated";
                        String message = changeDescription != null ? changeDescription
                                        : String.format("Test Order #%d has been updated", testOrder.getTestId());

                        NotificationEvent notificationEvent = createNotificationEvent(
                                        testOrder,
                                        "UPDATE",
                                        title,
                                        message,
                                        updatedBy);

                        // Send to notification queue
                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        "testorder.notification.updated",
                                        notificationEvent);

                        log.info("Published TestOrder updated notification: testOrderId={}", testOrder.getTestId());

                } catch (Exception e) {
                        log.error("Failed to publish TestOrder updated notification: testOrderId={}, error={}",
                                        testOrder.getTestId(), e.getMessage(), e);
                }
        }

        /**
         * Publish test order status changed notification
         */
        public void publishTestOrderStatusNotification(TestOrder testOrder, String oldStatus, String newStatus,
                        String changedBy) {
                try {
                        String title = "Test Order Status Changed";
                        String message = String.format("Test Order #%d status changed from %s to %s",
                                        testOrder.getTestId(), oldStatus, newStatus);

                        NotificationEvent notificationEvent = createNotificationEvent(
                                        testOrder,
                                        "STATUS_CHANGE",
                                        title,
                                        message,
                                        changedBy);

                        // Send to notification queue
                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        "testorder.notification.status",
                                        notificationEvent);

                        log.info("Published TestOrder status notification: testOrderId={}, oldStatus={}, newStatus={}",
                                        testOrder.getTestId(), oldStatus, newStatus);

                } catch (Exception e) {
                        log.error("Failed to publish TestOrder status notification: testOrderId={}, error={}",
                                        testOrder.getTestId(), e.getMessage(), e);
                }
        }

        /**
         * Publish test result available notification
         */
        public void publishTestResultNotification(TestOrder testOrder, String resultSummary, String createdBy) {
                try {
                        String title = "Test Results Available";
                        String message = String.format("Test results are now available for Test Order #%d. %s",
                                        testOrder.getTestId(), resultSummary);

                        NotificationEvent notificationEvent = createNotificationEvent(
                                        testOrder,
                                        "RESULT_AVAILABLE",
                                        title,
                                        message,
                                        createdBy);

                        // Send to notification queue
                        rabbitTemplate.convertAndSend(
                                        RabbitMQConfig.TESTORDER_EXCHANGE,
                                        "testorder.notification.result",
                                        notificationEvent);

                        log.info("Published TestResult notification: testOrderId={}", testOrder.getTestId());

                } catch (Exception e) {
                        log.error("Failed to publish TestResult notification: testOrderId={}, error={}",
                                        testOrder.getTestId(), e.getMessage(), e);
                }
        }

        /**
         * Create common notification event structure
         */
        private NotificationEvent createNotificationEvent(TestOrder testOrder, String action,
                        String title, String message, String createdBy) {
                // Set target privileges - adjust based on your privilege system
                Set<Long> targetPrivileges = new HashSet<>();
                targetPrivileges.add(1L); // View TestOrder privilege
                targetPrivileges.add(2L); // Manage TestOrder privilege

                // Add specific privileges based on action
                if ("RESULT_AVAILABLE".equals(action)) {
                        targetPrivileges.add(3L); // View Results privilege
                }

                return NotificationEvent.builder()
                                .eventId(UUID.randomUUID().toString())
                                .entityType("TEST_ORDER")
                                .entityId(testOrder.getTestId().toString())
                                .action(action)
                                .title(title)
                                .message(message)
                                .targetPrivileges(targetPrivileges)
                                .isGlobal(false) // Set to true if all users should see this
                                .isRead(false)
                                .createdAt(Instant.now())
                                .createdBy(createdBy)
                                .orderStatus(testOrder.getStatus())
                                .priority("NORMAL") // Default priority since TestOrder doesn't have priority field
                                .testType("BLOOD_TEST") // Default test type since TestOrder doesn't have testType field
                                .data(testOrder)
                                .build();
        }
}