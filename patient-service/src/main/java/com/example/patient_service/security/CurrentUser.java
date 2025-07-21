package com.example.patient_service.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentUser {
    private Long userId;
    private String username;
    private String email;
    private String identifyNum;
}

