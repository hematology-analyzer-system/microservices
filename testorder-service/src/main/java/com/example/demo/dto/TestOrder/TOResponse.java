package com.example.demo.dto.TestOrder;

import com.example.demo._enum.Gender;
import com.example.demo.dto.Result.MinimalResultResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TOResponse {
    private String status;

    private String createdBy;

    private String runBy;

    private LocalDateTime runAt;



    //Result involve

    private List<MinimalResultResponse> results;



    //Comment involve

    private String content;



    //Patient involve

    private String fullName;

    private String address;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String phone;
}
