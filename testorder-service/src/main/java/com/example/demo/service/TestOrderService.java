package com.example.demo.service;

import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.entity.Patient;
import com.example.demo.entity.TestOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.TestOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TestOrderService {
    @Autowired
    private TestOrderRepository testOrderRepository;

    private TOResponse toResponse(TestOrder testOrder) {
        Patient patient = testOrder.getPatient();

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
    };

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

        return toResponse(testOrder);
    }

    public void deteleTestOrder(Long TO_id){
        testOrderRepository.deleteById(TO_id);
    }
}
