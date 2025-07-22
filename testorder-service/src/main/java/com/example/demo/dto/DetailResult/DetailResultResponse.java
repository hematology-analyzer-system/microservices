package com.example.demo.dto.DetailResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailResultResponse {
    private String paramName;

    private String unit;

    private Double value;

    private Double rangeMin;
    private Double rangeMax;
}
