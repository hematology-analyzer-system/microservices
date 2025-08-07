package com.example.demo.repository;

import com.example.demo.entity.TestOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TestOrderRepository extends JpaRepository<TestOrder,Long>, JpaSpecificationExecutor<TestOrder> {

    Page<TestOrder> findByPatientTOIdIn(List<Integer> patientIds, Pageable pageable);
//    @EntityGraph(attributePaths = {"results"})
//    Optional<TestOrder> findById(Long id);
}
