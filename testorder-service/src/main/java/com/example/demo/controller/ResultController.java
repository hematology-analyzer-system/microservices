package com.example.demo.controller;

import com.example.demo.dto.DetailResult.DetailResultResponse;
import com.example.demo.dto.Result.ResultResponse;
import com.example.demo.dto.Result.ReviewResultRequest;
import com.example.demo.repository.ResultRepository;
import com.example.demo.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/result")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @Autowired
    private ResultRepository resultRepository;

    @PostMapping("/gen/{resultId}")
    public ResponseEntity<ResultResponse> genResult(
            @PathVariable("resultId") Long resultId
    ){
        ResultResponse resultResponse = resultService.genDetail(resultId);

        return ResponseEntity.ok(resultResponse);
    }

    @PutMapping("/{resultId}")
    public ResponseEntity<DetailResultResponse> reviewedResult(
            @PathVariable("resultId")  Long resultId,
            @RequestBody ReviewResultRequest reviewResultRequest
            ){
        DetailResultResponse resultResponse = resultService.reviewResult(resultId, reviewResultRequest);

        return  ResponseEntity.ok(resultResponse);
    }
}
