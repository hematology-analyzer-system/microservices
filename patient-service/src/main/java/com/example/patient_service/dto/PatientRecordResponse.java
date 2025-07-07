package com.example.patient_service.dto;

import com.example.patient_service._enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientRecordResponse {
    private Integer id;

    private String fullName;

    private String address;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String phone;

    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
