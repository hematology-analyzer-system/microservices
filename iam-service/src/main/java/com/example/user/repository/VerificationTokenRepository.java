package com.example.user.repository;

import com.example.user.model.User;
import com.example.user.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    @Modifying
    @Transactional
    void deleteByUser(User user); // Assuming your User model is in com.example.yourpackage.model
    @Modifying
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
