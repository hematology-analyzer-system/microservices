package com.example.user.config;

import com.example.user.model.User;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public AuditorAware<UserAuditInfo> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return Optional.empty(); // Không có user đang đăng nhập
            }

            String email = auth.getName(); // Spring Security mặc định là username/email
            User user = userRepository.findByEmailWithoutAuditing(email);
            if (user == null) {
                return Optional.empty(); // tránh NullPointerException
            }

            UserAuditInfo auditInfo = new UserAuditInfo(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getIdentifyNum()
            );

            return Optional.of(auditInfo);
        };
    }
}