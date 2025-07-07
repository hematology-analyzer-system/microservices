package com.example.user.repository;
import com.example.user.model.User;
import com.example.user.dto.userdto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    List<User> findByFullNameContainingIgnoreCase(String FullName);

    Optional<User> findByEmail(String email);
//    @Query("SELECT new com.example.user.dto.userdto.UserResponse(u.id,u.fullName, u.phone, u.email, u.gender, u.address,CAST(collect(r.name) AS set)) "+
//            "FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<UserResponse> searchByName(@Param("keyword") String keyword);
    Page<User> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
