package com.example.user.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuditInfo {
    private Long userId;
    private String fullName;
    private String email;
    private String identifyNum;
}
