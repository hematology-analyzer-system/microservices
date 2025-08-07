package com.example.user.repository;

import com.example.user.model.UserAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuditLogRepository extends MongoRepository<UserAuditLog, String> {
    
    /**
     * Find audit logs by action types (for notifications)
     */
    Page<UserAuditLog> findByActionIn(List<String> actions, Pageable pageable);
    
    /**
     * Count audit logs by action types (for notification count)
     */
    long countByActionIn(List<String> actions);
}
