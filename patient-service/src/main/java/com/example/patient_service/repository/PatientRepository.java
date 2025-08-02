package com.example.patient_service.repository;

import com.example.patient_service.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Page<Patient> findAll(Specification<Patient> spec, Pageable pageable);
}
