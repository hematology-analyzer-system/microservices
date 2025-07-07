package com.example.user.dto.userdto;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    private Integer age;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String fullName, String date_of_Birth, String email, String address, String gender, Integer age ) {
        this.fullName = fullName;
        this.date_of_Birth = date_of_Birth;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.age = age;
    }
}
