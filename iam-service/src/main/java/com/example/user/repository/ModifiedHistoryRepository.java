package com.example.user.repository;

import com.example.user.model.ModifiedHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModifiedHistoryRepository extends JpaRepository<ModifiedHistory, Long> {
}
