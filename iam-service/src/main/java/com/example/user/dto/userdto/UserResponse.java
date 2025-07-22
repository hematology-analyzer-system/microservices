package com.example.user.dto.userdto;
import com.example.user.model.Role;
import lombok.Data;


import java.util.Set;

@Data

public class UserResponse {
    private String fullName;
    private String email;
    private String phone;
    private String identifyNum;
    private String gender;
//    private Integer age;
    private String address;
    private String dateOfBirth;
    public UserResponse() {}
    public UserResponse( String fullName,  String email,String phone, String identifyNum, String gender,  String address, String dateOfBirth){
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.identifyNum = identifyNum;
        this.gender = gender;
//        this.age = age;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }
}