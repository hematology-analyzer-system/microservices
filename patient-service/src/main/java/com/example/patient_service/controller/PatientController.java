package com.example.patient_service.controller;

import com.example.patient_service.dto.AddPatientRequest;
import com.example.patient_service.dto.PatientRecordResponse;
import com.example.patient_service.dto.UpdatePatientRequest;
import com.example.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@AllArgsConstructor
@CrossOrigin("*")
public class PatientController {

    private final PatientService patientService;

    @PostMapping()
    public ResponseEntity<PatientRecordResponse> addPatientRecord(
            @Valid @RequestBody AddPatientRequest request
    ) {
        return ResponseEntity.ok(patientService.addPatientRecord(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientRecordResponse> updatePatientRecord(
            @Valid @RequestBody UpdatePatientRequest request,
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(patientService.updatePatientRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PatientRecordResponse> deletePatientRecord(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(patientService.deletePatientRecord(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientRecordResponse> getPatientRecord(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(patientService.getPatientRecord(id));
    }

    @GetMapping()
    public ResponseEntity<Page<PatientRecordResponse>> allPatientRecords(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(patientService.allPatientRecords(pageNo, pageSize, sortBy, sortDirection));
    }
}
