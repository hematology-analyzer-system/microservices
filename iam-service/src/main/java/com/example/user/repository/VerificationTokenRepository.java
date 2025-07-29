package com.example.user.repository;

import com.example.user.model.User;
import com.example.user.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user); // Assuming your User model is in com.example.yourpackage.model
    void deleteByExpiryDateBefore(LocalDateTime dateTime);

}
