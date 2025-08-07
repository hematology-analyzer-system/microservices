package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Entity to store message logs in MongoDB
 * Stores both incoming and outgoing messages for auditing and debugging
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "message_logs")
public class MessageLog {

    @Id
    private String id;

    // Message metadata
    private String messageId;
    private String messageType;
    private String source;
    private String correlationId;
    private Instant timestamp;

    // Direction of message
    private MessageDirection direction; // INCOMING or OUTGOING

    // RabbitMQ routing information
    private String exchange;
    private String routingKey;
    private String queue;

    // Message content
    private String payload;

    // Processing information
    private MessageStatus status; // RECEIVED, PROCESSED, FAILED
    private String errorMessage;
    private Instant processedAt;

    // Service information
    private String serviceName;

    public enum MessageDirection {
        INCOMING, OUTGOING
    }

    public enum MessageStatus {
        RECEIVED, PROCESSED, FAILED
    }
}