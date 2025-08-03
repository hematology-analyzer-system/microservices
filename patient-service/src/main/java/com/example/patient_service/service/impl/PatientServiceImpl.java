package com.example.patient_service.service.impl;

import com.example.patient_service.dto.AddPatientRequest;
import com.example.patient_service.dto.PatientRecordResponse;
import com.example.patient_service.dto.UpdatePatientRequest;
import com.example.patient_service.model.Patient;
import com.example.patient_service.rabbitmq.PatientRabbitMQProducer;
import com.example.patient_service.repository.PatientRepository;
import com.example.patient_service.security.CurrentUser;
import com.example.patient_service.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    // Attribute represents RabbitMQ producer to send event to Correct queues
    private final PatientRabbitMQProducer patientRabbitMQProducer;

    private String formatlizeCreatedBy(Long id, String name, String email, String identifyNum) {
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    @Override
    public PatientRecordResponse addPatientRecord(AddPatientRequest request) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Patient newPatient = Patient.builder()
                .fullName(request.getFullName())
                .address(request.getAddress())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .createdBy(formatlizeCreatedBy(
                        currentUser.getUserId(),
                        currentUser.getUsername(),
                        currentUser.getEmail(),
                        currentUser.getIdentifyNum()
                ))
                .updateBy(formatlizeCreatedBy(
                        currentUser.getUserId(),
                        currentUser.getUsername(),
                        currentUser.getEmail(),
                        currentUser.getIdentifyNum()
                ))
                .build();


        patientRepository.save(newPatient);

        // Send Add-patient-event to RabbitMQ
        patientRabbitMQProducer.sendAddPatientEvent(newPatient);

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
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        updatedPatient.setUpdateBy(formatlizeCreatedBy(
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getIdentifyNum()
        ));


        patientRepository.save(updatedPatient);

        // Send Update-patient-event to RabbitMQ
        patientRabbitMQProducer.sendUpdatePatientEvent(updatedPatient);

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

        // Sent Delete-patient-event to RabbitMQ
        patientRabbitMQProducer.sendDeletePatientEvent(deletePatient);

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

    public Page<PatientRecordResponse> getFilteredPatients(
            String searchText,
            String sortBy,
            String direction,
            int offsetPage,
            int limitOnePage
    ) {
        Pageable pageable = PageRequest.of(offsetPage - 1, limitOnePage,
                Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

        Specification<Patient> spec = (root, query, cb) -> cb.conjunction();

        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullName")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("phone")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("address")), "%" + searchText.toLowerCase() + "%")
            ));
        }

        Page<Patient> patients = patientRepository.findAll(spec, pageable);

        return patients.map(patient -> PatientRecordResponse.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build());
    }
}
