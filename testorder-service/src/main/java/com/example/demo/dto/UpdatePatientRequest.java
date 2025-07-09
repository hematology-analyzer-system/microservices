package com.example.demo.dto;

import com.example.demo._enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePatientRequest {

    private String fullName;

    private String address;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String phone;

    private String email;
}
