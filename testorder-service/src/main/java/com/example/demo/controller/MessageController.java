package com.example.demo.controller;

import com.example.demo.entity.MessageLog;
import com.example.demo.repository.MessageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controller to view message logs for debugging and monitoring
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageLogRepository messageLogRepository;

    /**
     * Get all message logs with pagination
     */
    @GetMapping
    public ResponseEntity<Page<MessageLog>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MessageLog> messages = messageLogRepository.findAll(pageable);

        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages by direction (INCOMING/OUTGOING)
     */
    @GetMapping("/direction/{direction}")
    public ResponseEntity<List<MessageLog>> getMessagesByDirection(
            @PathVariable String direction) {

        MessageLog.MessageDirection messageDirection;
        try {
            messageDirection = MessageLog.MessageDirection.valueOf(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        List<MessageLog> messages = messageLogRepository.findByDirection(messageDirection);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MessageLog>> getMessagesByStatus(
            @PathVariable String status) {

        MessageLog.MessageStatus messageStatus;
        try {
            messageStatus = MessageLog.MessageStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        List<MessageLog> messages = messageLogRepository.findByStatus(messageStatus);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get messages by correlation ID
     */
    @GetMapping("/correlation/{correlationId}")
    public ResponseEntity<List<MessageLog>> getMessagesByCorrelationId(
            @PathVariable String correlationId) {

        List<MessageLog> messages = messageLogRepository.findByCorrelationId(correlationId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get recent messages (last 24 hours)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<MessageLog>> getRecentMessages() {
        Instant oneDayAgo = Instant.now().minus(24, ChronoUnit.HOURS);
        Instant now = Instant.now();

        List<MessageLog> messages = messageLogRepository.findByTimestampBetween(oneDayAgo, now);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get message statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<MessageStats> getMessageStats() {
        long totalMessages = messageLogRepository.count();
        long incomingMessages = messageLogRepository.findByDirection(MessageLog.MessageDirection.INCOMING).size();
        long outgoingMessages = messageLogRepository.findByDirection(MessageLog.MessageDirection.OUTGOING).size();
        long failedMessages = messageLogRepository.findByStatus(MessageLog.MessageStatus.FAILED).size();
        long processedMessages = messageLogRepository.findByStatus(MessageLog.MessageStatus.PROCESSED).size();

        MessageStats stats = MessageStats.builder()
                .totalMessages(totalMessages)
                .incomingMessages(incomingMessages)
                .outgoingMessages(outgoingMessages)
                .failedMessages(failedMessages)
                .processedMessages(processedMessages)
                .build();

        return ResponseEntity.ok(stats);
    }

    /**
     * Simple DTO for message statistics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MessageStats {
        private long totalMessages;
        private long incomingMessages;
        private long outgoingMessages;
        private long failedMessages;
        private long processedMessages;
    }
}