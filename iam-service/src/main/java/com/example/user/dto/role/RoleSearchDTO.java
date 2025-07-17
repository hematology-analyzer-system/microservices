package com.example.user.dto.role;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

// Assuming you have a DTO package, e.g., com.example.yourproject.dto
@Data
public class RoleSearchDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Set<String> privileges; // Role privileges (descriptions or codes)

}
