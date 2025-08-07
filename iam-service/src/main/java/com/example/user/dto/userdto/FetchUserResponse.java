package com.example.user.dto.userdto;

import com.example.user.model.Role;
import com.example.user.model.User;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class FetchUserResponse {
    private String fullName;
    private String email;
    private String phone;
    private String identifyNum;
    private String gender;
    private String address;
    private String date_of_Birth;
    private String status;
    private List<Long> roleIds;
    private LocalDateTime updateAt;
    private String updated_by_email;

    public FetchUserResponse(User user, LocalDateTime updateAt, String updated_by_email) {
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.identifyNum = user.getIdentifyNum();
        this.gender = user.getGender();
        this.address = user.getAddress();
        this.status = user.getStatus();
        this.date_of_Birth = user.getDate_of_Birth();
        this.roleIds = user.getRoles().stream().map(Role::getRoleId).collect(Collectors.toList());
        this.updateAt = updateAt;
        this.updated_by_email = updated_by_email;
    }
}