package com.example.user.dto.search;

import lombok.Data;

import java.util.Set;

@Data
public class searchDTO {
    private Long id;
    private String fullName;
    private String email;
    private String gender;
    private String status;
    private String phone;
    private String address;
    private String createdAt;
    private String updatedAt;

    private Set<String> roles;
    private Set<String> privileges;
}

