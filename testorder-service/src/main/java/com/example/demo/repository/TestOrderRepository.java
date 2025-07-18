package com.example.demo.repository;

import com.example.demo.entity.TestOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestOrderRepository extends JpaRepository<TestOrder,Long> {
    Page<TestOrder> findByPatient_FullNameContainingIgnoreCase(String keyword, Pageable pageable);

//    @EntityGraph(attributePaths = {"results"})
//    Optional<TestOrder> findById(Long id);
}
