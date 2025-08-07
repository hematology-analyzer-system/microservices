package com.example.patient_service.service;

import com.example.patient_service.model.NotificationEvent;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientMongoService {
    List<NotificationEvent> getAllNotificationsOfCurrentUser();
    long countUnreadNotificationsOfCurrentUser();
    void markAsRead(String notificationId) throws ChangeSetPersister.NotFoundException;
    void markAllAsRead();
    Page<NotificationEvent> getPagingNotificationOfCurrentUser(Integer page, Integer size, String sortBy);
}
