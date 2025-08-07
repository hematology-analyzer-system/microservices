package com.example.patient_service.Server;

import com.example.grpc.patient.*;
import com.example.patient_service._enum.Gender;
import com.example.patient_service.model.Patient;
import com.example.patient_service.repository.PatientRepository;
import com.example.patient_service.service.EncryptionService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class P_Service extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository patientRepository;
    private final EncryptionService encryptionService;
    public static LocalDate safeParseDate(String date) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        for (DateTimeFormatter fmt : formatters) {
            try {
                return LocalDate.parse(date, fmt);
            } catch (DateTimeParseException ignored) {}
        }

        throw new IllegalArgumentException("Date of Birth is invalid: " + date);
    }

    @Override
    public void getPatientById(PatientRequest patientRequest, StreamObserver<PatientResponse> responseObserver) {

        System.out.println("getPatientById haha" + patientRequest.getId());


        Patient patient = patientRepository.findById(patientRequest.getId())
                .orElseThrow(() -> new StatusRuntimeException(Status.NOT_FOUND.withDescription("Patient Not Found")));

        PatientResponse myResponse = PatientResponse.newBuilder()
                .setDateOfBirth(String.valueOf(patient.getDateOfBirth()))
                .setFullName(patient.getFullName())
                .setAddress(patient.getAddress())
                .setGender(patient.getGender().name())
                .setPhone(patient.getPhone())
                .setId(patient.getId())
                .build();

        responseObserver.onNext(myResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void createPatient(CreatePatientRequest createpatientRequest, StreamObserver<PatientResponse> responseObserver) {
        Gender gender = createpatientRequest.getGender().equalsIgnoreCase("MALE") ? Gender.MALE : Gender.FEMALE;

        Patient patient = Patient.builder()
                .fullName(createpatientRequest.getFullName())
                .gender(gender)
                .email(createpatientRequest.getEmail())
                .phone(createpatientRequest.getPhone())
                .address(createpatientRequest.getAddress())
                .dateOfBirth(safeParseDate(createpatientRequest.getDateOfBirth()))
                .createdBy(createpatientRequest.getCreatedBy())
                .build();

        patientRepository.save(patient);

        PatientResponse response = PatientResponse.newBuilder()
                .setFullName(patient.getFullName())
                .setAddress(patient.getAddress())
                .setGender(gender.name())
                .setPhone(createpatientRequest.getPhone())
                .setDateOfBirth(createpatientRequest.getDateOfBirth())
                .setId(patient.getId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updatePatient(UpdatePatientRequestGRPC request, StreamObserver<PatientResponse> responseObserver){
        Patient patient = patientRepository.findById(request.getId())
                .orElseThrow(() -> new StatusRuntimeException(Status.NOT_FOUND.withDescription("Patient Not Found")));

        patient.setFullName(request.getFullName());
        patient.setAddress(request.getAddress());
        patient.setGender(request.getGender().equalsIgnoreCase("MALE") ? Gender.MALE : Gender.FEMALE);
        patient.setPhone(request.getPhone());
        patient.setDateOfBirth(safeParseDate(request.getDateOfBirth()));

        patientRepository.save(patient);

        PatientResponse response = PatientResponse.newBuilder()
                .setFullName(patient.getFullName())
                .setAddress(patient.getAddress())
                .setGender(patient.getGender().name())
                .setPhone(patient.getPhone())
                .setDateOfBirth(patient.getDateOfBirth().toString())
                .setId(patient.getId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchPatient(SearchPatientRequestGRPC requestGRPC, StreamObserver<SearchPatientResponseGRPC> responseObserver) {
        try {
            String keyword = requestGRPC.getKeyword();

            if (keyword == null || keyword.trim().isEmpty()) {
                // Return empty result for empty search
                SearchPatientResponseGRPC response = SearchPatientResponseGRPC.newBuilder()
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            // Since data is encrypted, we need to fetch all patients and filter after decryption
            List<Patient> allPatients = patientRepository.findAll();

            String searchKeyword = keyword.toLowerCase().trim();

            List<PatientResponse> patientResponses = allPatients.stream()
                    .filter(patient -> matchesSearchCriteria(patient, searchKeyword))
                    .map(this::mapToPatientResponse)
                    .collect(Collectors.toList());

            SearchPatientResponseGRPC response = SearchPatientResponseGRPC.newBuilder()
                    .addAllPatients(patientResponses)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error searching patients: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    /**
     * Check if patient matches search criteria after decryption
     */
    private boolean matchesSearchCriteria(Patient patient, String searchKeyword) {
        try {
            // Decrypt and check each searchable field
            String decryptedFullName = encryptionService.decrypt(patient.getFullName());
            if (decryptedFullName != null && decryptedFullName.toLowerCase().contains(searchKeyword)) {
                return true;
            }

            String decryptedPhone = encryptionService.decrypt(patient.getPhone());
            if (decryptedPhone != null && decryptedPhone.toLowerCase().contains(searchKeyword)) {
                return true;
            }

            String decryptedAddress = encryptionService.decrypt(patient.getAddress());
            if (decryptedAddress != null && decryptedAddress.toLowerCase().contains(searchKeyword)) {
                return true;
            }

//             Email if you have it encrypted
            if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
                String decryptedEmail = encryptionService.decrypt(patient.getEmail());
                if (decryptedEmail != null && decryptedEmail.toLowerCase().contains(searchKeyword)) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            // Log decryption error but don't fail the entire search
            System.err.println("Error decrypting patient data for ID " + patient.getId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Map Patient entity to PatientResponse (with encrypted data)
     */
    private PatientResponse mapToPatientResponse(Patient patient) {
        return PatientResponse.newBuilder()
                .setId(patient.getId())
                .setFullName(patient.getFullName()) // Keep encrypted
                .setPhone(patient.getPhone()) // Keep encrypted
                .setGender(patient.getGender().equals(Gender.MALE) ? "MALE" : "FEMALE")
                .setAddress(patient.getAddress()) // Keep encrypted
                .setDateOfBirth(patient.getDateOfBirth().toString())
//                .setEmail(patient.getEmail() != null ? patient.getEmail() : "") // Keep encrypted if exists
                .build();
    }


    @Override
    public void getPatientsByIds(PatientIdsRequest request, StreamObserver<PatientListResponse> responseObserver) {
        List<Integer> ids = request.getIdsList();
        List<Patient> patients = patientRepository.findAllById(ids);

        List<PatientResponse> patientResponses = patients.stream()
                .map(patient -> PatientResponse.newBuilder()
                        .setFullName(patient.getFullName())
                        .setPhone(patient.getPhone())
                        .setGender(patient.getGender().equals(Gender.MALE) ? "MALE" : "FEMALE")
                        .setAddress(patient.getAddress())
                        .setDateOfBirth(patient.getDateOfBirth().toString())
                        .setId(patient.getId())
                        .build())
                .toList();

        PatientListResponse response = PatientListResponse.newBuilder()
                .addAllPatients(patientResponses)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
