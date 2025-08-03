package com.example.user.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventMessage {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String eventType; // CREATED, UPDATED, DELETED
    private LocalDateTime timestamp;
    private String source; // iam-service
    
    public UserEventMessage(Long userId, String username, String email, String firstName, String lastName, String eventType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = "iam-service";
    }
}
