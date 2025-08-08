package com.example.user.repository;

import com.example.user.model.NotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Set;

public interface NotificationRepository extends MongoRepository<NotificationEvent, String> {
    
    // Find notifications by user privileges (same query as patient service)
    @Query("{$or: [ { 'isGlobal': true }, { 'targetPrivileges': { $in: ?0 } } ]}")
    Page<NotificationEvent> findByUserPrivileges(Set<Long> userPrivileges, Pageable pageable);
    
    // Count unread notifications by user privileges
    @Query(value = "{$and: [ { 'isRead': false }, {$or: [ { 'isGlobal': true }, { 'targetPrivileges': { $in: ?0 } } ]} ]}", count = true)
    long countUnreadByUserPrivileges(Set<Long> userPrivileges);
    
    // Find notifications by user privileges and read status
    @Query("{$and: [ { 'isRead': ?1 }, {$or: [ { 'isGlobal': true }, { 'targetPrivileges': { $in: ?0 } } ]} ]}")
    Page<NotificationEvent> findByUserPrivilegesAndIsRead(Set<Long> userPrivileges, Boolean isRead, Pageable pageable);
}
