package com.example.user.repository;

import com.example.user.model.UserAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuditLogRepository extends MongoRepository<UserAuditLog, String> {
}
