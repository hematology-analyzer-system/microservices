package com.example.user.dto.userdto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;

@Data
public class UpdateUserRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String date_of_Birth;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String address;
    @NotBlank
    private String gender;
    @NotBlank
    private String phone;
    @NotBlank
    private String status;
    @NotBlank
    private String identifyNum;
//    private String profilePic;

    private List<Long> roleIds;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String fullName, String date_of_Birth, String email, String address, String gender, String phone, String status, String identifyNum, List<Long> roleIds ) {
        this.fullName = fullName;
        this.date_of_Birth = date_of_Birth;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.identifyNum = identifyNum;
        this.status = status;
        this.roleIds = roleIds;
    }
}
