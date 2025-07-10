package com.example.user.repository;

import com.example.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
    Page<Role> findByCodeContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Role> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}