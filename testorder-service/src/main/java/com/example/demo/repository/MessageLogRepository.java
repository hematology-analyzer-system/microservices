package com.example.demo.repository;

import com.example.demo.entity.MessageLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB repository for MessageLog entities
 */
@Repository
public interface MessageLogRepository extends MongoRepository<MessageLog, String> {

    List<MessageLog> findByMessageId(String messageId);

    List<MessageLog> findByCorrelationId(String correlationId);

    List<MessageLog> findByDirection(MessageLog.MessageDirection direction);

    List<MessageLog> findByStatus(MessageLog.MessageStatus status);

    @Query("{'timestamp': {$gte: ?0, $lte: ?1}}")
    List<MessageLog> findByTimestampBetween(Instant start, Instant end);

    @Query("{'messageType': ?0, 'direction': ?1}")
    List<MessageLog> findByMessageTypeAndDirection(String messageType, MessageLog.MessageDirection direction);
}