package com.example.patient_service.dto;

import com.example.patient_service._enum.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
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
