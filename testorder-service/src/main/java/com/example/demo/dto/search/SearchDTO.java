package com.example.demo.dto.search;

import com.example.demo._enum.Gender;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.Period;

@Data
public class SearchDTO {
    private Long id;

    private String fullName;
    private Integer age;
    private Gender gender;
    private String phone;

    private String status;
    private String createdBy;
    private String runBy;
    private LocalDateTime runAt;
}
