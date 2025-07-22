package com.example.user.repository;
import com.example.user.model.User;
import com.example.user.dto.userdto.UserResponse;
import com.example.user.model.UserAuditInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//import java.lang.ScopedValue;
import java.time.LocalDateTime;
import java.util.*;


public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {


//public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    List<User> findByFullNameContainingIgnoreCase(String FullName);
    //    UserDetails findByEmail(String email);
    Optional<User> findByEmail(String email);
//    @Query("SELECT new com.example.user.dto.userdto.UserResponse(u.id,u.fullName, u.phone, u.email, u.gender, u.address,CAST(collect(r.name) AS set)) "+
//            "FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<UserResponse> searchByName(@Param("keyword") String keyword);
    Page<User> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByIdentifyNum(String identifyNum);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
    @Query("SELECT u.id AS userId, u.fullName AS fullName, u.email AS email, u.identifyNum AS identifyNum FROM User u WHERE u.email = :email")
    Optional<UserAuditProjection> findAuditInfoByEmail(@Param("email") String email);
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmailWithoutAuditing(@Param("email") String email);

    List<User> findByStatusAndCreatedAtBefore(String status, LocalDateTime create_at);
}
