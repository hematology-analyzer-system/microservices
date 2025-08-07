package com.example.patient_service.repository;

import com.example.patient_service.model.NotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Set;

public interface PatientMongoRepository extends MongoRepository<NotificationEvent, String> {
    @Query("{$or: [ { 'isGlobal': true }, { 'targetPrivileges': { $in: ?0 } } ]}")
    Page<NotificationEvent> findByUserPrivileges(Set<Long> userPrivileges, Pageable pageable);
}
