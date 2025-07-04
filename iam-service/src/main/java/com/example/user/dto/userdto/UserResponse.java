package com.example.user.dto.userdto;
import com.example.user.model.Role;
import lombok.Data;


import java.util.Set;

@Data

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Set<String> roles;
    public UserResponse() {}
    public UserResponse(Long id, String fullName, String phone, String email,
                        String gender, String address, Set<String> roles) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.roles = roles;
    }
}