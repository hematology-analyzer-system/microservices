package com.example.demo.dto.Result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinimalResultResponse {
    private String paramName;
    private String value;
    private String unit;
}
