package com.example.demo.service;

import com.example.demo.config.JwtProperties;
import com.example.demo.dto.Comment.MinimalCommentResponse;
import com.example.demo.dto.DetailResult.DetailResultResponse;
import com.example.demo.dto.Result.MinimalResultResponse;
import com.example.demo.dto.TestOrder.AddTORequest;
import com.example.demo.dto.TestOrder.PageTOResponse;
import com.example.demo.dto.TestOrder.TOResponse;
import com.example.demo.dto.TestOrder.UpdateTORequest;
import com.example.demo.dto.search.SearchDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.BadRequestException;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.TestOrderRepository;
import com.example.demo.security.CurrentUser;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@EnableConfigurationProperties({JwtProperties.class})
public class TestOrderService {
    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    private PatientRepository  patientRepository;

    private TOResponse toResponse(TestOrder testOrder) {
        Patient patient = testOrder.getPatient();

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

                .fullName(patient.getFullName())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
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

        String createdByinString = formalizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        Patient patient = id.map(pid -> patientRepository.findById(pid)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found")))
                .orElseGet(() -> {
                    Patient newPatient = Patient.builder()
                            .fullName(addTORequest.getFullName())
                            .dateOfBirth(addTORequest.getDateOfBirth())
                            .gender(addTORequest.getGender())
                            .address(addTORequest.getAddress())
                            .phone(addTORequest.getPhoneNumber())
                            .email(addTORequest.getEmail())
                            .build();
                    return patientRepository.save(newPatient);
                });

        TestOrder testOrder = TestOrder.builder()
                .createdBy(createdByinString)
                .status("PENDING")
                .patient(patient)
                .build();

        patient.getTestOrders().add(testOrder);

        patientRepository.save(patient);

        return toResponse(testOrder);
    }

    public TOResponse modifyTO(Long TO_id, UpdateTORequest updateTO){
        TestOrder testOrder = testOrderRepository.findById(TO_id)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "TestOrder not found!"));

        Patient patient = testOrder.getPatient();
        if(patient == null){
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found!");
        }

        patient.setFullName(updateTO.getFullName());
        patient.setAddress(updateTO.getAddress());
        patient.setGender(updateTO.getGender());
        patient.setPhone(updateTO.getPhone());
        patient.setDateOfBirth(updateTO.getDateOfBirth());

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        testOrder.setUpdateBy(formalizeCreatedBy(
                currentUser.getUserId(),
                currentUser.getFullname(),
                currentUser.getEmail(),
                currentUser.getIdentifyNum()
        ));

        testOrderRepository.save(testOrder);

        return toResponse(testOrder);
    }

    public void deteleTestOrder(Long TO_id){
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
        Page<TestOrder>  testOrderPage;

        if(keyword == null || keyword.isBlank()){
            testOrderPage = testOrderRepository.findAll(pageable);
        }else{
            testOrderPage = testOrderRepository.findByPatient_FullNameContainingIgnoreCase(keyword, pageable);
        }

        List<TOResponse> testorderResponses = testOrderPage.getContent().stream()
                .map(this::toResponse)
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


        // Search
        if (searchText != null && !searchText.isEmpty()) {
            String search = "%" + searchText.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                Join<TestOrder, Patient> patientJoin = root.join("patient", JoinType.LEFT);

                return cb.or(
                        cb.like(cb.lower(root.get("status")), search),
                        cb.like(cb.lower(root.get("runBy")), search),
                        cb.like(cb.lower(root.get("createdBy")), search),
                        cb.like(cb.lower(patientJoin.get("fullName")), search)
                );
            });
        }

        //Filter
        if (filter != null && !filter.isEmpty()) {
            if(filter.containsKey("fromDate")){
                LocalDateTime fromDate = LocalDateTime.parse(filter.get("fromDate").toString());
                spec = spec.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("runAt"), fromDate));
            }

            if(filter.containsKey("toDate")){
                LocalDateTime toDate = LocalDateTime.parse(filter.get("toDate").toString());
                spec = spec.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("runAt"), toDate));
            }

        }

        Page<TestOrder>  testOrders = testOrderRepository.findAll(spec, pageable);

        return testOrders.map(tOrder -> {
            SearchDTO searchDTO = new SearchDTO();

            searchDTO.setId(tOrder.getTestId());

            searchDTO.setFullName(tOrder.getPatient().getFullName());
            searchDTO.setAge(
                    Period.between(tOrder.getPatient().getDateOfBirth(), LocalDate.now()).getYears()
            );
            searchDTO.setGender(tOrder.getPatient().getGender());
            searchDTO.setPhone(tOrder.getPatient().getPhone());

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

        if(!testOrder.getStatus().equalsIgnoreCase("COMPLETED")){
            throw new BadRequestException("Test Order Status is Not Completed");
        }

        if(testOrder.getResults().isEmpty()){
            throw new BadRequestException("Test Order Results is empty!");
        }

        return toResponse(testOrder);
    }

}
