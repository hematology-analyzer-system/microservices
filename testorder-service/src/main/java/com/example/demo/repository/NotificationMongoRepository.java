package com.example.demo.repository;

import com.example.demo.entity.NotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * MongoDB repository for NotificationEvent
 * Provides methods for privilege-based notification filtering
 */
@Repository
public interface NotificationMongoRepository extends MongoRepository<NotificationEvent, String> {
    
    /**
     * Find notifications that user can see based on privileges
     * User can see notifications if:
     * 1. isGlobal = true (everyone can see)
     * 2. User has at least one of the target privileges
     */
    @Query("{ $or: [ " +
           "   { 'isGlobal': true }, " +
           "   { 'targetPrivileges': { $in: ?0 } } " +
           "] }")
    Page<NotificationEvent> findByUserPrivileges(Set<Long> userPrivileges, Pageable pageable);
    
    /**
     * Find all notifications for a user (non-paginated)
     */
    @Query("{ $or: [ " +
           "   { 'isGlobal': true }, " +
           "   { 'targetPrivileges': { $in: ?0 } } " +
           "] }")
    List<NotificationEvent> findAllByUserPrivileges(Set<Long> userPrivileges);
    
    /**
     * Count unread notifications for a user
     */
    @Query(value = "{ $and: [ " +
                  "   { $or: [ " +
                  "       { 'isGlobal': true }, " +
                  "       { 'targetPrivileges': { $in: ?0 } } " +
                  "   ] }, " +
                  "   { 'isRead': false } " +
                  "] }", 
           count = true)
    Long countUnreadByUserPrivileges(Set<Long> userPrivileges);
    
    /**
     * Find notifications by entity type and ID
     */
    List<NotificationEvent> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);
    
    /**
     * Find recent notifications by entity type
     */
    List<NotificationEvent> findTop10ByEntityTypeOrderByCreatedAtDesc(String entityType);
    
    /**
     * Find notifications by action type
     */
    List<NotificationEvent> findByActionOrderByCreatedAtDesc(String action);
}