package com.example.user.repository;

import com.example.user.model.Privilege;
import com.example.user.model.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
    Page<Role> findByCodeContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Role> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Role> findAll(Specification<Role> spec, Pageable pageable);
    @Query("SELECT r.privileges FROM Role r WHERE r.roleId = :roleId")
    Set<Privilege> findPrivilegesByRoleId(@Param("roleId") Long roleId);
}