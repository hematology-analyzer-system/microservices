package com.example.patient_service.repository;

import com.example.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByFullNameContainingIgnoreCase(String keyword);

}
