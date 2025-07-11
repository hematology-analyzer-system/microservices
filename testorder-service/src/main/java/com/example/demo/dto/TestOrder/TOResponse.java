package com.example.demo.dto.TestOrder;

import com.example.demo._enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TOResponse {
    private String status;

    private String runBy;

    private LocalDateTime runAt;


    private String fullName;

    private String address;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String phone;
}
