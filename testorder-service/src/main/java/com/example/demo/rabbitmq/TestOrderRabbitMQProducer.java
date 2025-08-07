package com.example.demo.rabbitmq;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.messaging.envelope.MessageEnvelope;
import com.example.demo.messaging.testorder.event.TestOrderCreatedEvent;
import com.example.demo.messaging.testorder.event.TestOrderUpdatedEvent;
import com.example.demo.service.MessageLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
            MessageEnvelope<TestOrderCreatedEvent> messageEnvelope = MessageEnvelope.<TestOrderCreatedEvent>builder()
                    .id(UUID.randomUUID())
                    .type(RabbitMQConfig.TESTORDER_CREATED_ROUTING_KEY)
                    .source(SERVICE_NAME)
                    .timestamp(Instant.now())
                    .correlationId(correlationId != null ? correlationId : UUID.randomUUID().toString())
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
            MessageEnvelope<TestOrderUpdatedEvent> messageEnvelope = MessageEnvelope.<TestOrderUpdatedEvent>builder()
                    .id(UUID.randomUUID())
                    .type(RabbitMQConfig.TESTORDER_UPDATED_ROUTING_KEY)
                    .source(SERVICE_NAME)
                    .timestamp(Instant.now())
                    .correlationId(correlationId != null ? correlationId : UUID.randomUUID().toString())
                    .payload(event)
                    .build();

            // Log message before sending
            messageLogService.logOutgoingMessage(messageEnvelope,
                    RabbitMQConfig.TESTORDER_EXCHANGE,
                    RabbitMQConfig.TESTORDER_UPDATED_ROUTING_KEY);

            // Send message
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TESTORDER_EXCHANGE,
                    messageEnvelope.getType(),
                    messageEnvelope);

            log.info("Published TestOrder updated event: testOrderId={}, status={}, correlationId={}",
                    event.getTestOrderId(), event.getStatus(), messageEnvelope.getCorrelationId());

        } catch (Exception e) {
            log.error("Failed to publish TestOrder updated event: testOrderId={}, error={}",
                    event.getTestOrderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish TestOrder updated event", e);
        }
    }
}