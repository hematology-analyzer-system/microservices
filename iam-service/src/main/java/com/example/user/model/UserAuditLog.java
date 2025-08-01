package com.example.user.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rabbitmq_messages")
public class UserAuditLog {
    @Id
    private String id;
    private Long userId;
    private String username; // Added field for username
    private String fullName;
    private String email;
    private String identifyNum;
    private String action;
    private String details;
    private String timestamp;

    public void setUserName(String username) {
        this.username = username;
    }
}
