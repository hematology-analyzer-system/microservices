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

    @NotBlank
    private String identify_num;

    private String address;

    private String gender;

    private String password;
    private String date_of_Birth;
//    private Integer age;
    public CreateUserRequest() {
    }

    public CreateUserRequest(String fullName, String email, String phone,String identify_num, String address, String gender, String password, String date_of_Birth, Integer age ) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.identify_num = identify_num;
        this.address = address;
        this.gender = gender;
        this.password = password;
        this.date_of_Birth = date_of_Birth;
//        this.age = age;
    }
}