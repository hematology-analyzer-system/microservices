package com.example.user.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEventMessage {
    private Long roleId;
    private String roleName;
    private List<String> privileges;
    private String eventType; // CREATED, UPDATED, DELETED
    private LocalDateTime timestamp;
    private String source; // iam-service
    
    public RoleEventMessage(Long roleId, String roleName, List<String> privileges, String eventType) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.privileges = privileges;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = "iam-service";
    }
}
