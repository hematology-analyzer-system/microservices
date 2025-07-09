package com.example.demo.repository;

import com.example.demo.entity.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestOrderRepository extends JpaRepository<TestOrder,Long> {
}
