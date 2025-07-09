package com.example.demo.service;

import com.example.demo.dto.AddPatientRequest;
import com.example.demo.dto.AddPatientRequest;
import com.example.demo.dto.PatientRecordResponse;
import com.example.demo.dto.UpdatePatientRequest;
import org.springframework.data.domain.Page;

public interface PatientService {
    // Define methods for patient service operations
    PatientRecordResponse addPatientRecord(AddPatientRequest request);
    PatientRecordResponse updatePatientRecord(Integer id, UpdatePatientRequest request);
    PatientRecordResponse deletePatientRecord(Integer id);
    PatientRecordResponse getPatientRecord(Integer id);
    Page<PatientRecordResponse> allPatientRecords(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
}
