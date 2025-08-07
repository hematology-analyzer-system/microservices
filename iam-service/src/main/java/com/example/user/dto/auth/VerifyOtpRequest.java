package com.example.user.dto.auth;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    String email;
    String otp;
}
