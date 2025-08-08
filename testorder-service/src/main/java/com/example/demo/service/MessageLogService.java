package com.example.demo.service;

import com.example.demo.entity.MessageLog;
import com.example.demo.messaging.envelope.MessageEnvelope;
import com.example.demo.repository.MessageLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service to handle message logging to MongoDB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageLogService {

    private final MessageLogRepository messageLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log outgoing message
     */
    public void logOutgoingMessage(MessageEnvelope<?> messageEnvelope, String exchange, String routingKey) {
        try {
            System.out.println("logOutgoingMessage 1");
            String payload = objectMapper.writeValueAsString(messageEnvelope);

            MessageLog messageLog = MessageLog.builder()
                    .messageId(messageEnvelope.getId().toString())
                    .messageType(messageEnvelope.getType())
                    .source(messageEnvelope.getSource())
                    .correlationId(messageEnvelope.getCorrelationId())
                    .timestamp(messageEnvelope.getTimestamp())
                    .direction(MessageLog.MessageDirection.OUTGOING)
                    .exchange(exchange)
                    .routingKey(routingKey)
                    .queue("")
                    .payload(payload)
                    .status(MessageLog.MessageStatus.PROCESSED)
                    .serviceName("testorder-service")
                    .processedAt(Instant.now())
                    .build();
            System.out.println("logOutgoingMessage 2");
            try {
                messageLogRepository.save(messageLog);
                System.out.println("logOutgoingMessage 3");
            } catch (Exception e) {
                System.err.println("Lỗi khi lưu messageLog: " + e.getMessage());
                e.printStackTrace(); // In toàn bộ stack trace để bạn biết lỗi ở đâu
            }

            log.info("TRACE publish id={} type={} correlation={} exchange={} routingKey={}",
                    messageEnvelope.getId(), messageEnvelope.getType(),
                    messageEnvelope.getCorrelationId(), exchange, routingKey);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for logging: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to log outgoing message: {}", e.getMessage());
        }
    }

    /**
     * Log incoming message
     */
    public void logIncomingMessage(MessageEnvelope<?> messageEnvelope, String queue) {
        try {
            String payload = objectMapper.writeValueAsString(messageEnvelope);

            MessageLog messageLog = MessageLog.builder()
                    .messageId(messageEnvelope.getId().toString())
                    .messageType(messageEnvelope.getType())
                    .source(messageEnvelope.getSource())
                    .correlationId(messageEnvelope.getCorrelationId())
                    .timestamp(messageEnvelope.getTimestamp())
                    .direction(MessageLog.MessageDirection.INCOMING)
                    .queue(queue)
                    .payload(payload)
                    .status(MessageLog.MessageStatus.RECEIVED)
                    .serviceName("testorder-service")
                    .processedAt(Instant.now())
                    .build();

            messageLogRepository.save(messageLog);

            log.info("TRACE consume id={} type={} correlation={} queue={}",
                    messageEnvelope.getId(), messageEnvelope.getType(),
                    messageEnvelope.getCorrelationId(), queue);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for logging: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to log incoming message: {}", e.getMessage());
        }
    }

    /**
     * Mark message as processed successfully
     */
    public void markMessageProcessed(String messageId) {
        try {
            messageLogRepository.findByMessageId(messageId)
                    .forEach(messageLog -> {
                        messageLog.setStatus(MessageLog.MessageStatus.PROCESSED);
                        messageLog.setProcessedAt(Instant.now());
                        messageLogRepository.save(messageLog);
                    });
        } catch (Exception e) {
            log.error("Failed to mark message as processed: {}", e.getMessage());
        }
    }

    /**
     * Mark message as failed
     */
    public void markMessageFailed(String messageId, String errorMessage) {
        try {
            messageLogRepository.findByMessageId(messageId)
                    .forEach(messageLog -> {
                        messageLog.setStatus(MessageLog.MessageStatus.FAILED);
                        messageLog.setErrorMessage(errorMessage);
                        messageLog.setProcessedAt(Instant.now());
                        messageLogRepository.save(messageLog);
                    });
        } catch (Exception e) {
            log.error("Failed to mark message as failed: {}", e.getMessage());
        }
    }
}