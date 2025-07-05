package com.example.user.repository;

import com.example.user.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByCode(String code);

}
