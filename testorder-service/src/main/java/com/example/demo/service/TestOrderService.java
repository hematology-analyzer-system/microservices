package com.example.demo.service;
import com.example.demo.Client.ClientRunner;
import com.example.demo._enum.Gender;
import com.example.demo.config.JwtProperties;
import com.example.demo.dto.Comment.MinimalCommentResponse;
import com.example.demo.dto.DetailResult.DetailResultResponse;
import com.example.demo.dto.PatientRecordResponse;
import com.example.demo.dto.Result.MinimalResultResponse;
import com.example.demo.dto.TestOrder.AddTORequest;
import com.example.demo.dto.TestOrder.PageTOResponse;
import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.dto.search.SearchDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ApiException;
import com.example.demo.service.EncryptionService;
import com.example.demo.repository.TestOrderRepository;
import com.example.demo.security.CurrentUser;
import com.example.grpc.patient.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableConfigurationProperties({JwtProperties.class})
public class TestOrderService {
    private final TestOrderRepository testOrderRepository;
    private final PatientServiceGrpc.PatientServiceBlockingStub stub;
    private final EncryptionService encryptionService;
    public TestOrderService(TestOrderRepository testOrderRepository,EncryptionService encryptionService) {
        this.testOrderRepository = testOrderRepository;
        this.stub = ClientRunner.getStub(); // lấy stub từ ClientRunner
        this.encryptionService = encryptionService;
    }

    @PreDestroy
    public void onShutdown() {
        ClientRunner.shutdown();
    }

    private PatientResponse encryptPatientResponse(PatientResponse originalResponse) {
        return PatientResponse.newBuilder()
                .setId(originalResponse.getId())
                .setFullName(encryptionService.encrypt(originalResponse.getFullName()))
                .setAddress(encryptionService.encrypt(originalResponse.getAddress()))
                .setPhone(encryptionService.encrypt(originalResponse.getPhone()))
                .setDateOfBirth(originalResponse.getDateOfBirth()) // Don't encrypt date
                .setGender(originalResponse.getGender()) // Don't encrypt gender
                .build();
    }

//    private CreatePatientRequest decryptPatientResponse(CreatePatientRequest patient) {
//        return CreatePatientRequest.builder()
////                .id(patient.getId())
//                .fullName(encryptionService.decrypt(patient.getFullName()))
//                .address(encryptionService.decrypt(patient.getAddress()))
//                .email(encryptionService.decrypt(patient.getEmail()))
//                .phone(encryptionService.decrypt(patient.getPhone()))
//                .dateOfBirth(patient.getDateOfBirth()) // Date is not encrypted
//                .gender(patient.getGender()) // Gender is not encrypted
//                .build();
//    }

    public TOResponse testGrpc(Integer id){
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("host.docker.internal", 9091)
                .usePlaintext()
                .build();

        try {
            PatientServiceGrpc.PatientServiceBlockingStub stub = PatientServiceGrpc.newBlockingStub(channel);

            PatientResponse response = stub.getPatientById(
                    PatientRequest.newBuilder().setId(id).build());

            return TOResponse.builder()
                    .fullName(response.getFullName())
                    .dateOfBirth(LocalDate.parse(response.getDateOfBirth()))
                    .address(response.getAddress())
                    .gender(response.getGender().equalsIgnoreCase("MALE") ? Gender.MALE : Gender.FEMALE)
                    .phone(response.getPhone())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("gRPC call failed: " + e.getMessage(), e);
        }
    }

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

    private TOResponse toResponse(TestOrder testOrder, PatientResponse patientResponse) {
        List<CommentTO> comment = testOrder.getCommentTO();
        List<Result> temp = testOrder.getResults();

        List<MinimalResultResponse> resultResponses = null;
        List<MinimalCommentResponse> commentResponses = null;

        // Map Result thanh MinResultDTO
        if(temp != null) {
            resultResponses = temp.stream()
                    .map(result -> {
                        List<DetailResultResponse> detailResponses = result.getDetailResults().stream()
                                .map(detail -> {
                                    DetailResultResponse d = new DetailResultResponse();
                                    d.setParamName(detail.getParamName());
                                    d.setUnit(detail.getUnit());
                                    d.setValue(detail.getValue());
                                    d.setRangeMin(detail.getRangeMin());
                                    d.setRangeMax(detail.getRangeMax());

                                    return d;
                                }).collect(Collectors.toList());

                        List<MinimalCommentResponse> commentResult = result.getComment().stream()
                                .map( detail -> {
                                    MinimalCommentResponse d = new MinimalCommentResponse();
                                    d.setId(detail.getCommentId());
                                    d.setUpdateBy(detail.getUpdateBy());
                                    d.setContent(detail.getContent());
                                    d.setCreatedBy(detail.getCreateBy());
                                    d.setCreatedAt(detail.getCreatedAt());

                                    return d;
                                }).toList();

                        MinimalResultResponse minimalResultResponse = new MinimalResultResponse();
                        minimalResultResponse.setId(result.getResultId());
                        minimalResultResponse.setReviewed(result.getReviewed());
                        minimalResultResponse.setUpdateBy(result.getUpdateBy());
                        minimalResultResponse.setDetailResults(detailResponses);
                        minimalResultResponse.setComment_result(commentResult);
                        minimalResultResponse.setCreatedAt(result.getCreatedAt());

                        return minimalResultResponse;
                    })
                    .collect(Collectors.toList());
        }
        // Map CommentTestOrder thanh DTO
        if(comment != null) {
            commentResponses = comment.stream()
                    .map(t -> {
                        MinimalCommentResponse res = new MinimalCommentResponse();
                        res.setId(t.getCommentId());
                        res.setContent(t.getContent());
                        res.setCreatedBy(t.getCreateBy());
                        res.setUpdateBy(t.getUpdateBy());
                        res.setCreatedAt(t.getCreatedAt());
                        return res;
                    })
                    .collect(Collectors.toList());
        }

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return TOResponse.builder()
                .testId(testOrder.getTestId())
                .status(testOrder.getStatus())
                .updateBy(formalizeCreatedBy(
                        currentUser.getUserId(),
                        currentUser.getFullname(),
                        currentUser.getEmail(),
                        currentUser.getIdentifyNum()
                ))
                .runBy(testOrder.getRunBy())
                .runAt(testOrder.getRunAt())

                .results(resultResponses)

                .comments(commentResponses)

                .fullName(encryptionService.decrypt(patientResponse.getFullName())) // Use decrypted data
                .address(encryptionService.decrypt(patientResponse.getAddress()))
                .gender(patientResponse.getGender().equalsIgnoreCase("MALE") ? Gender.MALE : Gender.FEMALE)
                .dateOfBirth(safeParseDate(patientResponse.getDateOfBirth()))
                .phone(encryptionService.decrypt(patientResponse.getPhone()))
                .build();
    }

    private String formalizeCreatedBy(Long id, String name, String email, String identifyNum){
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    public TOResponse createTO(AddTORequest  addTORequest, Optional<Integer> id) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(2L)&&!userPrivileges.contains(3L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to createTO");
        }

        String createdByinString = formalizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());


        try {

            PatientResponse response = id.map(pid -> {
                PatientResponse originalResponse = stub.getPatientById(PatientRequest.newBuilder().setId(pid).build());
                return encryptPatientResponse(originalResponse); // Encrypt after receiving
            }).orElseGet(() -> {
                        CreatePatientRequest createPatientRequest = CreatePatientRequest.newBuilder()
                                .setFullName(addTORequest.getFullName())
                                .setEmail(addTORequest.getEmail())
                                .setAddress(addTORequest.getAddress())
                                .setGender(addTORequest.getGender().equals(Gender.MALE) ? "MALE" : "FEMALE")
                                .setPhone(addTORequest.getPhoneNumber())
                                .setDateOfBirth(addTORequest.getDateOfBirth().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
                                .setCreatedBy(createdByinString)
                                .build();

                        return stub.createPatient(createPatientRequest);
                    });

            TestOrder testOrder = TestOrder.builder()
                    .createdBy(createdByinString)
                    .status("PENDING")
                    .patientTOId(response.getId())
                    .build();

            testOrderRepository.save(testOrder);

            return toResponse(testOrder, response);
        } catch (Exception e) {
            throw new RuntimeException("gRPC call failed aduni: " + e.getMessage(), e);
        }
    }



    public TOResponse modifyTO(Long TO_id, UpdateTORequest updateTO){

        TestOrder testOrder = testOrderRepository.findById(TO_id)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "TestOrder not found!"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(3L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to modifyTO");
        }


        try {

            UpdatePatientRequestGRPC request = UpdatePatientRequestGRPC.newBuilder()
                    .setId(testOrder.getPatientTOId())
                    .setFullName(updateTO.getFullName())
                    .setAddress(updateTO.getAddress())
                    .setGender(updateTO.getGender().equals(Gender.MALE) ? "MALE" : "FEMALE")
                    .setPhone(updateTO.getPhone())
                    .setDateOfBirth(updateTO.getDateOfBirth().toString())
                    .build();

            PatientResponse modifyResponse = stub.updatePatient(request);


            return toResponse(testOrder, modifyResponse);
        } catch (Exception e) {
            throw new RuntimeException("gRPC call failed: " + e.getMessage(), e);
        }
    }

    public void deteleTestOrder(Long TO_id){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(4L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to deleteTO");
        }

        testOrderRepository.deleteById(TO_id);
    }

    public PageTOResponse searchTestOrder(
            int page,
            int size,
            String sortBy,
            String direction,
            String keyword
    ){
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TestOrder> testOrderPage;
        List<TOResponse> testorderResponses;

        if (keyword == null || keyword.isBlank()) {
            testOrderPage = testOrderRepository.findAll(pageable);
        } else {

            SearchPatientResponseGRPC patientResponseGRPC = stub.searchPatient(
                    SearchPatientRequestGRPC.newBuilder().setKeyword(keyword).build()
            );

            List<Integer> patientIds = patientResponseGRPC.getPatientsList().stream()
                    .map(PatientResponse::getId)
                    .toList();

            if (patientIds.isEmpty()) {
                testOrderPage = Page.empty(pageable);
            } else {
                testOrderPage = testOrderRepository.findByPatientTOIdIn(patientIds, pageable);
            }
        }

            // Gọi gRPC cho từng testOrder
            testorderResponses = testOrderPage.getContent().stream()
                    .map(testOrder -> {
                        try {
                            PatientResponse patient = stub.getPatientById(
                                    PatientRequest.newBuilder().setId(testOrder.getPatientTOId()).build()
                            );
                            return toResponse(testOrder, patient);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to get patient via gRPC for TestOrder id = " + testOrder.getTestId(), e);
                        }
                    })
                    .toList();


        if(testorderResponses.isEmpty()){
            return PageTOResponse.empty(page, size, sortBy, direction);
        }

        return new PageTOResponse(
                testorderResponses,
                testOrderPage.getTotalElements(),
                page,
                size,
                sortBy,
                direction
        );
    }

    public Page<SearchDTO> getFilterTO (
        String searchText,
        Map<String, Object> filter,
        String sortBy,
        String direction,
        int offSetPage,
        int limitOnePage
    ){
        Pageable pageable = PageRequest.of(offSetPage - 1, limitOnePage,
                Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

        Specification<TestOrder> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());


        List<Integer> tempPatientIds = new ArrayList<>();
        SearchPatientResponseGRPC cachedResponse = null;
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(1L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to get filterTO");
        }

        // Search
        if (searchText != null && !searchText.isEmpty()) {
            // TIM CAC PATIENT THOA DIEU KIEN SEARCH
            SearchPatientResponseGRPC patientResponseGRPC = stub.searchPatient(
                    SearchPatientRequestGRPC.newBuilder().setKeyword(searchText).build()
            );

            cachedResponse = patientResponseGRPC;

            // LAY ID CUA PATIENT THOA DIEU KIEN
            List<Integer> patientIds = patientResponseGRPC.getPatientsList().stream()
                    .map(PatientResponse::getId)
                    .toList();

            tempPatientIds.addAll(patientIds);

            spec = spec.and((root, query, cb) -> {
                if (!patientIds.isEmpty()) {
                    return root.get("patientTOId").in(patientIds);
                } else {
                    return cb.disjunction();
                }
            });
        }


        // FILTER
        if (filter != null && !filter.isEmpty()) {
            if(filter.containsKey("fromDate")){
                LocalDateTime fromDate;
                try {
                    // Handle both LocalDateTime string and date string formats
                    String fromDateStr = filter.get("fromDate").toString();
                    if (fromDateStr.contains("T")) {
                        // Already in LocalDateTime format
                        fromDate = LocalDateTime.parse(fromDateStr);
                    } else {
                        // Convert date string to LocalDateTime (start of day)
                        fromDate = LocalDate.parse(fromDateStr).atStartOfDay();
                    }

                    spec = spec.and((root, query, cb) ->
                            cb.greaterThanOrEqualTo(root.get("runAt"), fromDate));
                } catch (Exception e) {
                    // Log error but continue without this filter
                    System.err.println("Error parsing fromDate: " + filter.get("fromDate"));
                }
            }

            if(filter.containsKey("toDate")){
                LocalDateTime toDate;
                try {
                    // Handle both LocalDateTime string and date string formats
                    String toDateStr = filter.get("toDate").toString();
                    if (toDateStr.contains("T")) {
                        // Already in LocalDateTime format
                        toDate = LocalDateTime.parse(toDateStr);
                    } else {
                        // Convert date string to LocalDateTime (end of day)
                        toDate = LocalDate.parse(toDateStr).atTime(23, 59, 59);
                    }

                    spec = spec.and((root, query, cb) ->
                            cb.lessThanOrEqualTo(root.get("runAt"), toDate));
                } catch (Exception e) {
                    // Log error but continue without this filter
                    System.err.println("Error parsing toDate: " + filter.get("toDate"));
                }
            }
        }

        Page<TestOrder> testOrders = testOrderRepository.findAll(spec, pageable);

        if (testOrders.isEmpty()) return Page.empty(pageable);

        Map<Integer, PatientResponse> patientMap;

        if (!tempPatientIds.isEmpty()) {
            patientMap = cachedResponse.getPatientsList().stream()
                    .collect(Collectors.toMap(PatientResponse::getId, Function.identity()));
        } else {
            Set<Integer> allPatientIds = testOrders.stream()
                    .map(TestOrder::getPatientTOId)
                    .collect(Collectors.toSet());

            if (allPatientIds.isEmpty()) {
                patientMap = Map.of(); // empty map
            } else {
                PatientListResponse patientListResponse = stub.getPatientsByIds(
                        PatientIdsRequest.newBuilder().addAllIds(allPatientIds).build()
                );

                patientMap = patientListResponse.getPatientsList().stream()
                        .collect(Collectors.toMap(PatientResponse::getId, Function.identity()));
            }
        }

        return testOrders.map(tOrder -> {
            SearchDTO searchDTO = new SearchDTO();
            searchDTO.setId(tOrder.getTestId());

            PatientResponse patient = patientMap.get(tOrder.getPatientTOId());

            if (patient != null) {
                searchDTO.setFullName(encryptionService.decrypt(patient.getFullName()));
                searchDTO.setAge(Period.between(LocalDate.parse(patient.getDateOfBirth()), LocalDate.now()).getYears());
                searchDTO.setGender(patient.getGender().equalsIgnoreCase("MALE") ? Gender.MALE : Gender.FEMALE);
                searchDTO.setPhone(encryptionService.decrypt(patient.getPhone()));
            } else {
                searchDTO.setFullName("Unknown");
            }

            searchDTO.setStatus(tOrder.getStatus());
            searchDTO.setCreatedBy(tOrder.getCreatedBy());
            searchDTO.setRunBy(tOrder.getRunBy());
            searchDTO.setRunAt(tOrder.getRunAt());

            return searchDTO;
        });
    }

    public TOResponse viewDetail(Long id){
        TestOrder testOrder = testOrderRepository.findById(id)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "TestOrder not found!"));
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        Set<Long> userPrivileges = currentUser.getPrivileges();
        if (!userPrivileges.contains(1L)&&!userPrivileges.contains(5L)) {
            throw new AccessDeniedException("User does not have sufficient privileges to view detail TO");
        }
//        if(!testOrder.getStatus().equalsIgnoreCase("COMPLETED")){
//            throw new BadRequestException("Test Order Status is Not Completed");
//        }
//
//        if(testOrder.getResults().isEmpty()){
//            throw new BadRequestException("Test Order Results is empty!");
//        }


        PatientResponse patient = stub.getPatientById(PatientRequest.newBuilder()
                .setId(testOrder.getPatientTOId()).build());

        return toResponse(testOrder, patient);
    }

}
