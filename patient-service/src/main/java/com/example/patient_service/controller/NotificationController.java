package com.example.patient_service.controller;

import com.example.patient_service.model.NotificationEvent;
import com.example.patient_service.service.PatientMongoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private PatientMongoService patientMongoService;

    @GetMapping
    public ResponseEntity<List<NotificationEvent>> getNotifications() {
        return ResponseEntity.ok(patientMongoService.getAllNotificationsOfCurrentUser());
    }

    @GetMapping("paging")
    public ResponseEntity<Page<NotificationEvent>> getPagingNotifications(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size
    ) {
        return ResponseEntity.ok(patientMongoService.getPagingNotificationOfCurrentUser(page, size, "createdAt"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(patientMongoService.countUnreadNotificationsOfCurrentUser());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) throws ChangeSetPersister.NotFoundException {
        patientMongoService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead() {
        patientMongoService.markAllAsRead();
        return ResponseEntity.ok().build();
    }
}
