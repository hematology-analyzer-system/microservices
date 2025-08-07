package com.example.demo.dto.Result;

import com.example.demo.dto.DetailResult.DetailResultResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultResponse {
    private Boolean reviewed;

    private List<DetailResultResponse> resultList;
}
