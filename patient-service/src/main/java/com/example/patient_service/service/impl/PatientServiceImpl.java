package com.example.patient_service.service.impl;

import com.example.patient_service.dto.AddPatientRequest;
import com.example.patient_service.dto.PatientRecordResponse;
import com.example.patient_service.dto.UpdatePatientRequest;
import com.example.patient_service.model.Patient;
import com.example.patient_service.rabbitmq.PatientRabbitMQProducer;
import com.example.patient_service.repository.PatientRepository;
import com.example.patient_service.security.CurrentUser;
import com.example.patient_service.service.EncryptionService;
import com.example.patient_service.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    // Attribute represents RabbitMQ producer to send event to Correct queues
    private final PatientRabbitMQProducer patientRabbitMQProducer;
    private final EncryptionService encryptionService;

    private String formatlizeCreatedBy(Long id, String name, String email, String identifyNum) {
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    private Patient encryptPatientData(Patient patient) {
        patient.setFullName(encryptionService.encrypt(patient.getFullName()));
        patient.setAddress(encryptionService.encrypt(patient.getAddress()));
        patient.setEmail(encryptionService.encrypt(patient.getEmail()));
        patient.setPhone(encryptionService.encrypt(patient.getPhone()));
        return patient;
    }

    // Helper method to decrypt patient data for response
    private PatientRecordResponse decryptPatientResponse(Patient patient) {
        return PatientRecordResponse.builder()
                .id(patient.getId())
                .fullName(encryptionService.decrypt(patient.getFullName()))
                .address(encryptionService.decrypt(patient.getAddress()))
                .email(encryptionService.decrypt(patient.getEmail()))
                .phone(encryptionService.decrypt(patient.getPhone()))
                .dateOfBirth(patient.getDateOfBirth()) // Date is not encrypted
                .gender(patient.getGender()) // Gender is not encrypted
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

    @Override
    public PatientRecordResponse addPatientRecord(AddPatientRequest request) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(2L) && !userPrivileges.contains(3L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to add patient records");
        }
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

        encryptPatientData(newPatient);
        patientRepository.save(newPatient);

        // Send Add-patient-event to RabbitMQ
        patientRabbitMQProducer.sendAddPatientEvent(newPatient);

        return decryptPatientResponse(newPatient);
    }

    @Override
    public PatientRecordResponse updatePatientRecord(Integer id, UpdatePatientRequest request) {
        Patient updatedPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(3L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to add patient records");
        }
        updatedPatient.setFullName(encryptionService.encrypt(request.getFullName()));
        updatedPatient.setAddress(encryptionService.encrypt(request.getAddress()));
        updatedPatient.setEmail(encryptionService.encrypt(request.getEmail()));
        updatedPatient.setPhone(encryptionService.encrypt(request.getPhone()));
        updatedPatient.setDateOfBirth(request.getDateOfBirth());
        updatedPatient.setGender(request.getGender());
        updatedPatient.setUpdateBy(formatlizeCreatedBy(
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getIdentifyNum()
        ));


        patientRepository.save(updatedPatient);

        // Send Update-patient-event to RabbitMQ
        patientRabbitMQProducer.sendUpdatePatientEvent(updatedPatient);

        return decryptPatientResponse(updatedPatient);
    }

    @Override
    public PatientRecordResponse deletePatientRecord(Integer id) {
        Patient deletePatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(4L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to add patient records");
        }
        patientRepository.deleteById(id);

        // Sent Delete-patient-event to RabbitMQ
        patientRabbitMQProducer.sendDeletePatientEvent(deletePatient);

        return decryptPatientResponse(deletePatient);
    }

    @Override
    public PatientRecordResponse getPatientRecord(Integer id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(1L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to add patient records");
        }
        return decryptPatientResponse(patient);
    }

    @Override
    public Page<PatientRecordResponse> allPatientRecords(
            Integer pageNo, Integer pageSize, String sortBy, String sortDir
    ) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(1L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to add patient records");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Patient> patientPage = patientRepository.findAll(pageable);

        return patientPage.map(this::decryptPatientResponse);

    }

    public Page<PatientRecordResponse> getFilteredPatients(
            String searchText,
            String sortBy,
            String direction,
            int offsetPage,
            int limitOnePage
    ) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(1L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to add patient records");
        }
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

        return patients.map(this::decryptPatientResponse);
    }
}
