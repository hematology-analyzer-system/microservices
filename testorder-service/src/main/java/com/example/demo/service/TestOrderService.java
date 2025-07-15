package com.example.demo.service;

import com.example.demo.config.JwtProperties;
import com.example.demo.dto.Result.MinimalResultResponse;
import com.example.demo.dto.TestOrder.PageTOResponse;
import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Patient;
import com.example.demo.entity.Result;
import com.example.demo.entity.TestOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.TestOrderRepository;
import com.example.demo.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@EnableConfigurationProperties({JwtProperties.class})
public class TestOrderService {
    @Autowired
    private TestOrderRepository testOrderRepository;

    private TOResponse toResponse(TestOrder testOrder) {
        Patient patient = testOrder.getPatient();
        Comment comment = testOrder.getComment();

        List<MinimalResultResponse> resultResponses = testOrder.getResults().stream()
                .map(result -> {
                    MinimalResultResponse res = new MinimalResultResponse();
                    res.setValue(result.getValue());
                    res.setUnit(result.getUnit());
                    return res;
                })
                .collect(Collectors.toList());


        return TOResponse.builder()
                .status(testOrder.getStatus())
                .runBy(testOrder.getRunBy())
                .runAt(testOrder.getRunAt())
                .fullName(patient.getFullName())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
                .build();
    }

    private String formatlizeCreatedBy(Long id, String name, String email, String identifyNum){
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    public TOResponse modifyTO(Long TO_id, UpdateTORequest updateTO){
        TestOrder testOrder = testOrderRepository.findById(TO_id)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "TestOrder not found!"));

        Patient patient = testOrder.getPatient();
        if(patient == null){
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found!");
        }

        patient.setFullName(updateTO.getFullName());
        patient.setAddress(updateTO.getAddress());
        patient.setGender(updateTO.getGender());
        patient.setPhone(updateTO.getPhone());
        patient.setDateOfBirth(updateTO.getDateOfBirth());

        testOrderRepository.save(testOrder);

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        testOrder.setUpdateBy(formatlizeCreatedBy(
                currentUser.getUserId(),
                currentUser.getFullname(),
                currentUser.getEmail(),
                currentUser.getIdentifyNum()
        ));

        return toResponse(testOrder);
    }

    public void deteleTestOrder(Long TO_id){
        testOrderRepository.deleteById(TO_id);
    }

    public PageTOResponse searchTestOrder(int page, int size, String sortBy, String direction, String keyword){
        Sort sort = sortBy.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TestOrder>  testOrderPage;

        if(keyword == null || keyword.isBlank()){
            testOrderPage = testOrderRepository.findAll(pageable);
        }else{
            testOrderPage = testOrderRepository.findByPatientNameContainingIgnoreCase(keyword, pageable);
        }

        List<TOResponse> testorderResponses = testOrderPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        if(testorderResponses.isEmpty()){
            return PageTOResponse.empty(page, size, sortBy, direction);
        }

        return new PageTOResponse(
                testorderResponses,
                testOrderPage.getTotalElements(),
                page,
                size,
                sortBy,
                direction
        );
    }

}
