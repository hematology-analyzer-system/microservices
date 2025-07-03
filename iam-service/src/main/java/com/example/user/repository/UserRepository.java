package com.example.user.repository;
import com.example.user.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    List<User> findByFullNameContainingIgnoreCase(String FullName);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
