package com.example.demo.service;

import com.example.demo.config.JwtProperties;
import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.entity.Patient;
import com.example.demo.entity.TestOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.TestOrderRepository;
import com.example.demo.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@EnableConfigurationProperties({JwtProperties.class})
public class TestOrderService {
    @Autowired
    private TestOrderRepository testOrderRepository;

    private TOResponse toResponse(TestOrder testOrder, String update) {
        Patient patient = testOrder.getPatient();

        return TOResponse.builder()
                .status(testOrder.getStatus())
                .runBy(update)
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

        return toResponse(testOrder, formatlizeCreatedBy(
                currentUser.getUserId(),
                currentUser.getFullname(),
                currentUser.getEmail(),
                currentUser.getIdentifyNum()));
    }

    public void deteleTestOrder(Long TO_id){
        testOrderRepository.deleteById(TO_id);
    }
}
