package com.example.patient_service.service;

import com.example.patient_service.dto.AddPatientRequest;
import com.example.patient_service.dto.PatientRecordResponse;
import com.example.patient_service.dto.UpdatePatientRequest;
import org.springframework.data.domain.Page;

public interface PatientService {
    // Define methods for patient service operations
    PatientRecordResponse addPatientRecord(AddPatientRequest request);
    PatientRecordResponse updatePatientRecord(Integer id, UpdatePatientRequest request);
    PatientRecordResponse deletePatientRecord(Integer id);
    PatientRecordResponse getPatientRecord(Integer id);
    Page<PatientRecordResponse> allPatientRecords(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    Page<PatientRecordResponse> getFilteredPatients(String searchText,
                                                    String sortBy,
                                                    String direction,
                                                    int offsetPage,
                                                    int limitOnePage);
}
