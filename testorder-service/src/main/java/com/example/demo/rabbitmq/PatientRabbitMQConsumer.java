package com.example.demo.rabbitmq;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.messaging.envelope.MessageEnvelope;
import com.example.demo.service.MessageLogService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer for Patient events in TestOrder service
 * Consumes patient events from other services
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatientRabbitMQConsumer {

    private final MessageLogService messageLogService;

    /**
     * Handle patient created events
     */
    @RabbitListener(queues = RabbitMQConfig.PATIENT_CREATED_QUEUE)
    public void onPatientCreated(MessageEnvelope<JsonNode> messageEnvelope) {
        try {
            // Log incoming message
            messageLogService.logIncomingMessage(messageEnvelope, RabbitMQConfig.PATIENT_CREATED_QUEUE);

            JsonNode patientData = messageEnvelope.getPayload();

            log.info("Received patient created event: patientId={}, correlationId={}",
                    patientData.get("patientId"), messageEnvelope.getCorrelationId());

            // TODO: Process patient creation - update local cache, validate test orders,
            // etc.
            processPatientCreated(patientData, messageEnvelope.getCorrelationId());

            // Mark message as processed
            messageLogService.markMessageProcessed(messageEnvelope.getId().toString());

        } catch (Exception e) {
            log.error("Failed to process patient created event: messageId={}, error={}",
                    messageEnvelope.getId(), e.getMessage(), e);

            // Mark message as failed
            messageLogService.markMessageFailed(messageEnvelope.getId().toString(), e.getMessage());
            throw e;
        }
    }

    /**
     * Handle patient updated events
     */
    @RabbitListener(queues = RabbitMQConfig.PATIENT_UPDATED_QUEUE)
    public void onPatientUpdated(MessageEnvelope<JsonNode> messageEnvelope) {
        try {
            // Log incoming message
            messageLogService.logIncomingMessage(messageEnvelope, RabbitMQConfig.PATIENT_UPDATED_QUEUE);

            JsonNode patientData = messageEnvelope.getPayload();

            log.info("Received patient updated event: patientId={}, correlationId={}",
                    patientData.get("patientId"), messageEnvelope.getCorrelationId());

            // TODO: Process patient update - update local cache, validate test orders, etc.
            processPatientUpdated(patientData, messageEnvelope.getCorrelationId());

            // Mark message as processed
            messageLogService.markMessageProcessed(messageEnvelope.getId().toString());

        } catch (Exception e) {
            log.error("Failed to process patient updated event: messageId={}, error={}",
                    messageEnvelope.getId(), e.getMessage(), e);

            // Mark message as failed
            messageLogService.markMessageFailed(messageEnvelope.getId().toString(), e.getMessage());
            throw e;
        }
    }

    /**
     * Process patient created event
     */
    private void processPatientCreated(JsonNode patientData, String correlationId) {
        // Implementation depends on business requirements
        // Examples:
        // - Update local patient cache
        // - Validate existing test orders for this patient
        // - Send notifications

        log.info("Processing patient created: patientId={}, correlationId={}",
                patientData.get("patientId"), correlationId);
    }

    /**
     * Process patient updated event
     */
    private void processPatientUpdated(JsonNode patientData, String correlationId) {
        // Implementation depends on business requirements
        // Examples:
        // - Update local patient cache
        // - Update related test orders
        // - Send notifications if patient info affects test results

        log.info("Processing patient updated: patientId={}, correlationId={}",
                patientData.get("patientId"), correlationId);
    }
}