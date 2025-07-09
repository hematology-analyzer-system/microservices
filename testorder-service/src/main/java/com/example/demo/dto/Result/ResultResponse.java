package com.example.demo.dto.Result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultResponse {
    private Boolean reviewed;

    private String value;

    private String unit;

    private String rangeMin;

    private String rangeMax;
}
