package com.example.user.dto.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateUserRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    private String address;

    private String gender;

    private String password;
    private String DoB;
    private Integer age;
    public CreateUserRequest() {
    }

    public CreateUserRequest(String fullName, String email, String phone, String address, String gender, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.password = password;
    }
}