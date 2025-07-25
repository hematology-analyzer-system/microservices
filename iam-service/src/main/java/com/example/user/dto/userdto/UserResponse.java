package com.example.user.dto.userdto;

import lombok.Data;


import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private String fullName;
    private String email;
    private String phone;
    private String identifyNum;
    private String gender;
    private String address;
    private String date_of_Birth;
//    private Set<Long> roleIds;
//    private LocalDateTime updateAt;
//    private String updated_by_email;

    public UserResponse( String fullName,  String email,String phone, String identifyNum, String gender,  String address, String date_of_Birth) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.identifyNum = identifyNum;
        this.gender = gender;
        this.address = address;
        this.date_of_Birth = date_of_Birth;
//        this.roleIds = roleIds;
//        this.updateAt = updateAt;
//        this.updated_by_email = updated_by_email;
    }
}