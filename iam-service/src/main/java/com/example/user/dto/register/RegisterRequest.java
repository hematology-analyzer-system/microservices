package com.example.user.dto.register;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String Date_of_Birth;
//    private Integer age;
    private String address;
    private String password;
//    private String status;
    private String identifyNum;
}
