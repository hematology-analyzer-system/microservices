package com.example.demo.dto.TestOrder;

import com.example.demo._enum.Gender;
import com.example.demo.dto.Comment.MinimalCommentResponse;
import com.example.demo.dto.Result.MinimalResultResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    private String updateBy;

    private String runBy;

    private LocalDateTime runAt;



    //Result involve

    private List<MinimalResultResponse> results;



    //Comment involve

    private List<MinimalCommentResponse> comments;



    //Patient involve

    private String fullName;

    private String address;

    private Gender gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate dateOfBirth;

    private String phone;
}
