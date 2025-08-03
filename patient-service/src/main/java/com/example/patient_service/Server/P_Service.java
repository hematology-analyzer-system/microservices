package com.example.patient_service.Server;

import com.example.grpc.patient.*;
import com.example.patient_service._enum.Gender;
import com.example.patient_service.model.Patient;
import com.example.patient_service.repository.PatientRepository;
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

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class P_Service extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository patientRepository;

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
    public void searchPatient(SearchPatientRequestGRPC requestGRPC, StreamObserver<SearchPatientResponseGRPC> responseObserver){
        List<Patient> fitPatient = patientRepository.findByFullNameContainingIgnoreCase(requestGRPC.getKeyword());


        List<PatientResponse> patientResponses = fitPatient.stream()
                .map( patient -> PatientResponse.newBuilder()
                        .setFullName(patient.getFullName())
                        .setPhone(patient.getPhone())
                        .setGender(patient.getGender().equals(Gender.MALE) ? "MALE" : "FEMALE")
                        .setAddress(patient.getAddress())
                        .setDateOfBirth(patient.getDateOfBirth().toString())
                        .setId(patient.getId())
                        .build()).toList();

        SearchPatientResponseGRPC response = SearchPatientResponseGRPC.newBuilder()
                .addAllPatients(patientResponses)
                                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
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
