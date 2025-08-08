package com.example.patient_service.controller;

import com.example.patient_service.dto.AddPatientRequest;
import com.example.patient_service.dto.PatientRecordResponse;
import com.example.patient_service.dto.UpdatePatientRequest;
import com.example.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","https://netlify.khoa.email"})
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
    @GetMapping("/filter")
    public ResponseEntity<Page<PatientRecordResponse>> getFilteredPatients(
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Page<PatientRecordResponse> patients = patientService.getFilteredPatients(
                    searchText, sortBy, direction, page, size);

            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}