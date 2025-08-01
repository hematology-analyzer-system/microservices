package com.example.patient_service.repository;

import com.example.patient_service.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByFullNameContainingIgnoreCase(String keyword);

    Page<Patient> findAll(Specification<Patient> spec, Pageable pageable);
}
