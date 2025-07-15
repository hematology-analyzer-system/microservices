package com.example.demo.repository;

import com.example.demo.entity.TestOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestOrderRepository extends JpaRepository<TestOrder,Long> {
    Page<TestOrder> findByPatientNameContainingIgnoreCase(String keyword, Pageable pageable);
}
