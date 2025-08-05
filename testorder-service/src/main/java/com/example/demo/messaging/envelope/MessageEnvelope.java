package com.example.demo.messaging.envelope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Common message envelope for RabbitMQ communication
 * Following the pattern from RabbitMQ.md specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEnvelope<T> {
    private UUID id;
    private String type;
    private String source;
    private Instant timestamp;
    private String correlationId;
    private T payload;
}