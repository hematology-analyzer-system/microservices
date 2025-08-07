package com.example.demo.controller;

import com.example.demo.dto.DetailResult.DetailResultResponse;
import com.example.demo.dto.Result.ResultResponse;
import com.example.demo.dto.Result.ReviewResultRequest;
import com.example.demo.repository.ResultRepository;
import com.example.demo.service.ResultService;
import com.example.demo.service.TestOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/result")
public class ResultController {

    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping("/gen/{testorderId}")
    public ResponseEntity<ResultResponse> genResult(
            @PathVariable("testorderId") Long testorderId
    ){
        ResultResponse resultResponse = resultService.genDetail(testorderId);

        return ResponseEntity.ok(resultResponse);
    }

    @PutMapping("/{resultId}")
    public ResponseEntity<List<DetailResultResponse>> updateAllDetails(
            @PathVariable Long resultId,
            @RequestBody List<ReviewResultRequest> payload
    ) {
        List<DetailResultResponse> updated = resultService.updateAllDetails(resultId, payload);
        return ResponseEntity.ok(updated);
    }
}
