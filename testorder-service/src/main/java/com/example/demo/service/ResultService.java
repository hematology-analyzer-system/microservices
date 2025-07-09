package com.example.demo.service;

import com.example.demo.dto.Result.ResultResponse;
import com.example.demo.dto.Result.ReviewResultRequest;
import com.example.demo.entity.Result;
import com.example.demo.entity.TestOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.ResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResultService {
    private ResultRepository resultRepository;

    private boolean isValueInRange(String value, String rangeMin, String rangeMax){
        try{
            double val = Double.parseDouble(value);
            double min = Double.parseDouble(rangeMin);
            double max = Double.parseDouble(rangeMax);

            return val >= min && val <= max;
        }
        catch (Exception e){
            throw new BadRequestException("Value is Not In Range");
        }
    }


    public ResultResponse reviewResult (Long resultId, ReviewResultRequest reviewResultRequest){
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Result not found"));

        TestOrder testOrder = result.getTestOrder();

        String value = reviewResultRequest.getValue();

        if(!testOrder.getStatus().equals("COMPLETED")){
            throw new BadRequestException("Test Order Status is Not Completed");
        }

        if(!isValueInRange(value, result.getRangeMin(), result.getRangeMax())){
            throw new BadRequestException("Value is Not In Range");
        }

        result.setValue(value);
        result.setReviewed(true);

        testOrder.setStatus("REVIEWED");
        resultRepository.save(result);
        return ResultResponse.builder()
                .reviewed(result.getReviewed())
                .value(result.getValue())
                .unit(result.getUnit())
                .rangeMin(result.getRangeMin())
                .rangeMax(result.getRangeMax())
                .build();
    }
}
