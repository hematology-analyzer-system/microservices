package com.example.demo.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentUser {
    private Long userId;
    private String fullname;
    private String email;
    private String identifyNum;
    private Set<Long> privileges;
}
