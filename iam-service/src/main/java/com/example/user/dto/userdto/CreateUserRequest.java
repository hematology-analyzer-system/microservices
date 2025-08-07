package com.example.user.dto.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateUserRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String identifyNum;

    private String address;

    private String gender;

    private String password;
    private String date_of_Birth;
    private List<Long> roleIds;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String fullName, String email, String phone,String identifyNum, String address, String gender, String password, String date_of_Birth, List<Long> roleIds) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.identifyNum = identifyNum;
        this.address = address;
        this.gender = gender;
        this.password = password;
        this.date_of_Birth = date_of_Birth;
        this.roleIds = roleIds;
    }
}