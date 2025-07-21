package com.example.demo.service;

import com.example.demo._enum.Gender;
import com.example.demo.dto.DetailResult.DetailResultResponse;
import com.example.demo.dto.Result.ResultResponse;
import com.example.demo.dto.Result.ReviewResultRequest;
import com.example.demo.entity.*;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.ResultRepository;
import com.example.demo.repository.TestOrderRepository;
import com.example.demo.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ResultService {
    private ResultRepository resultRepository;
    private TestOrderRepository testOrderRepository;

    private String formatlizeCreatedBy(Long id, String name, String email, String identifyNum){
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    private static final List<DetailParam> detailParams = List.of(
            new DetailParam("WBC", 4.0, 10.0, "cells/μL", Gender.MALE),
            new DetailParam("WBC", 4.0, 10.0, "cells/μL", Gender.FEMALE),

            new DetailParam("RBC", 4.7, 6.1, "million/μL", Gender.MALE),
            new DetailParam("RBC", 4.2, 5.4, "million/μL", Gender.FEMALE),

            new DetailParam("Hb/HGB", 14.0, 18.0, "g/dL", Gender.MALE),
            new DetailParam("Hb/HGB", 12.0, 16.0, "g/dL", Gender.FEMALE),

            new DetailParam("HCT", 42.0, 52.0, "%", Gender.MALE),
            new DetailParam("HCT", 37.0, 47.0, "%", Gender.FEMALE),

            new DetailParam("PLT", 150.0, 350.0, "cells/μL", Gender.MALE),
            new DetailParam("PLT", 150.0, 350.0, "cells/μL", Gender.FEMALE),

            new DetailParam("MCV", 80.0, 100.0, "fL", Gender.MALE),
            new DetailParam("MCV", 80.0, 100.0, "fL", Gender.FEMALE),

            new DetailParam("MCH", 27.0, 33.0, "pg", Gender.MALE),
            new DetailParam("MCH", 27.0, 33.0, "pg", Gender.FEMALE),

            new DetailParam("MCHC", 32.0, 36.0, "g/dL", Gender.MALE),
            new DetailParam("MCHC", 32.0, 36.0, "g/dL", Gender.FEMALE)
    );

    public DetailResult genDetailResult(String name, double min, double max, String unit) {
        double value = Math.round((Math.random() * (max - min) + min) * 10.0) / 10.0;

        return DetailResult.builder()
                .paramName(name)
                .value(value)
                .unit(unit)
                .rangeMin(min)
                .rangeMax(max)
                .build();
    }

    public List<DetailResult> genAllDetailResult(Gender gender){
        return detailParams.stream()
                .filter(d -> d.gender().equals(gender))
                .map(d -> genDetailResult(d.name(), d.min(), d.max(), d.unit()))
                .toList();
    }

    public ResultResponse genDetail(Long testOrderId){
        TestOrder testOrder = testOrderRepository.findById(testOrderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Test Order not found"));

        if(!testOrder.getStatus().equalsIgnoreCase("PENDING")){
            throw new BadRequestException("Test Order Status must be PENDING");
        }

        Result result = new Result();

        result.setTestOrder(testOrder);
        testOrder.getResults().add(result);

        Patient patient = testOrder.getPatient();

        CurrentUser currentUser = (CurrentUser)SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        List<DetailResult> detailResults = genAllDetailResult(patient.getGender());



        for (DetailResult detail : detailResults) {
            detail.setResult(result);
        }

        result.setDetailResults(detailResults);
        result.setReviewed(false);

        testOrder.setStatus("COMPLETED");
        testOrder.setRunBy(createdByinString);

        testOrderRepository.save(testOrder);

        List<DetailResultResponse> detailResultResponses = result.getDetailResults().stream()
                .map(res -> {
                    DetailResultResponse d = new DetailResultResponse();
                    d.setParamName(res.getParamName());
                    d.setUnit(res.getUnit());
                    d.setRangeMin(res.getRangeMin());
                    d.setRangeMax(res.getRangeMax());
                    d.setValue(res.getValue());

                    return d;
                }).toList();

        return ResultResponse.builder()
                .reviewed(result.getReviewed())
                .resultList(detailResultResponses)
                .build();
    }

    public DetailResultResponse reviewResult (Long resultId, ReviewResultRequest reviewResultRequest){
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Result not found"));

        TestOrder testOrder = result.getTestOrder();

        Double value = reviewResultRequest.getValue();

        if(!testOrder.getStatus().equalsIgnoreCase("COMPLETED")){
            throw new BadRequestException("Test Order Status is Not Completed");
        }

        DetailResult d = result.getDetailResults().stream()
                .filter(detailResult -> detailResult.getParamName().equalsIgnoreCase(reviewResultRequest.getParamName()))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Detail Result not found"));

        if(value > d.getRangeMax() || value < d.getRangeMin()){
            throw new BadRequestException("Value is Not In Range");
        }

        CurrentUser currentUser = (CurrentUser)SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());


        d.setValue(value);
        result.setReviewed(true);
        result.setUpdateBy(createdByinString);

        testOrder.setStatus("REVIEWED");


        resultRepository.save(result);
        testOrderRepository.save(testOrder);

        return DetailResultResponse.builder()
                .paramName(d.getParamName())
                .value(d.getValue())
                .unit(d.getUnit())
                .rangeMin(d.getRangeMin())
                .rangeMax(d.getRangeMax())
                .build();
    }
}
