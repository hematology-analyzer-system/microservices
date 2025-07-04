package com.example.user.dto.userdto;
import lombok.Data;
@Data
public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String gender;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String fullName, String phone, String email, String address, String gender) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
    }
}
