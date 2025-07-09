package com.example.demo.service.impl;

import com.example.demo.dto.AddPatientRequest;
import com.example.demo.dto.PatientRecordResponse;
import com.example.demo.dto.UpdatePatientRequest;
import com.example.demo.entity.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientRecordResponse addPatientRecord(AddPatientRequest request) {
        Patient newPatient = Patient.builder()
                .fullName(request.getFullName())
                .address(request.getAddress())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .build();

        patientRepository.save(newPatient);

        return PatientRecordResponse.builder()
                .id(newPatient.getId())
                .fullName(newPatient.getFullName())
                .address(newPatient.getAddress())
                .email(newPatient.getEmail())
                .phone(newPatient.getPhone())
                .dateOfBirth(newPatient.getDateOfBirth())
                .gender(newPatient.getGender())
                .createdAt(newPatient.getCreatedAt())
                .updatedAt(newPatient.getUpdatedAt())
                .build();
    }

    @Override
    public PatientRecordResponse updatePatientRecord(Integer id, UpdatePatientRequest request) {
        Patient updatedPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        updatedPatient.setFullName(request.getFullName());
        updatedPatient.setAddress(request.getAddress());
        updatedPatient.setEmail(request.getEmail());
        updatedPatient.setPhone(request.getPhone());
        updatedPatient.setDateOfBirth(request.getDateOfBirth());
        updatedPatient.setGender(request.getGender());

        patientRepository.save(updatedPatient);

        return PatientRecordResponse.builder()
                .id(updatedPatient.getId())
                .fullName(updatedPatient.getFullName())
                .address(updatedPatient.getAddress())
                .email(updatedPatient.getEmail())
                .phone(updatedPatient.getPhone())
                .dateOfBirth(updatedPatient.getDateOfBirth())
                .gender(updatedPatient.getGender())
                .createdAt(updatedPatient.getCreatedAt())
                .updatedAt(updatedPatient.getUpdatedAt())
                .build();
    }

    @Override
    public PatientRecordResponse deletePatientRecord(Integer id) {
        Patient deletePatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        patientRepository.deleteById(id);

        return PatientRecordResponse.builder()
                .id(deletePatient.getId())
                .fullName(deletePatient.getFullName())
                .address(deletePatient.getAddress())
                .email(deletePatient.getEmail())
                .phone(deletePatient.getPhone())
                .dateOfBirth(deletePatient.getDateOfBirth())
                .gender(deletePatient.getGender())
                .createdAt(deletePatient.getCreatedAt())
                .updatedAt(deletePatient.getUpdatedAt())
                .build();
    }

    @Override
    public PatientRecordResponse getPatientRecord(Integer id) {

        Patient newPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        return PatientRecordResponse.builder()
                .id(newPatient.getId())
                .fullName(newPatient.getFullName())
                .address(newPatient.getAddress())
                .email(newPatient.getEmail())
                .phone(newPatient.getPhone())
                .dateOfBirth(newPatient.getDateOfBirth())
                .gender(newPatient.getGender())
                .createdAt(newPatient.getCreatedAt())
                .updatedAt(newPatient.getUpdatedAt())
                .build();
    }

    @Override
    public Page<PatientRecordResponse> allPatientRecords(
            Integer pageNo, Integer pageSize, String sortBy, String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Patient> patientPage = patientRepository.findAll(pageable);

        return patientPage.map(patient -> PatientRecordResponse.builder()
                    .id(patient.getId())
                    .fullName(patient.getFullName())
                    .address(patient.getAddress())
                    .email(patient.getEmail())
                    .phone(patient.getPhone())
                    .dateOfBirth(patient.getDateOfBirth())
                    .gender(patient.getGender())
                    .createdAt(patient.getCreatedAt())
                    .updatedAt(patient.getUpdatedAt())
                    .build()
        );
    }
}
