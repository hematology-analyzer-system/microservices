package com.example.patient_service.service.impl;

import com.example.patient_service.model.NotificationEvent;
import com.example.patient_service.repository.PatientMongoRepository;
import com.example.patient_service.security.CurrentUser;
import com.example.patient_service.service.PatientMongoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PatientMongoServiceIml implements PatientMongoService {

    @Autowired
    private PatientMongoRepository patientMongoRepository;

    @Override
    public List<NotificationEvent> getAllNotificationsOfCurrentUser() {
        try {
            CurrentUser currentUser = getCurrentUser();
            Set<Long> userPrivileges = currentUser.getPrivileges();

            return patientMongoRepository.findAll().stream()
                    .filter(event -> event.getIsGlobal() ||
                            event.getTargetPrivileges().stream().anyMatch(userPrivileges::contains))
                    .sorted(Comparator.comparing(NotificationEvent::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Exception in PatientMongoServiceIml.getAllNotificationsOfCurrentUser");
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<NotificationEvent> getPagingNotificationOfCurrentUser(Integer page, Integer size, String sortBy) {
        CurrentUser currentUser = getCurrentUser();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        Pageable myPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return patientMongoRepository.findByUserPrivileges(userPrivileges, myPageable);
    }

    @Override
    public long countUnreadNotificationsOfCurrentUser() {
        return getAllNotificationsOfCurrentUser().stream()
                .filter(event -> !event.getIsRead())
                .count();
    }

    @Override
    public void markAsRead(String notificationId) throws ChangeSetPersister.NotFoundException {
        NotificationEvent event = patientMongoRepository.findById(notificationId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        event.setIsRead(true);
        patientMongoRepository.save(event);
    }

    @Override
    public void markAllAsRead() {
        List<NotificationEvent> events = getAllNotificationsOfCurrentUser();
        events.forEach(event -> {
            event.setIsRead(true);
            patientMongoRepository.save(event);
        });
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getDetails();
    }
}
