package com.example.user.config;

import com.example.user.model.UserAuditInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
public class AuditTestConfig {


    @Bean(name = "auditorProvider")
    public AuditorAware<UserAuditInfo> auditorProvider() {
        return () -> Optional.of(new UserAuditInfo(
                99L, "Test User", "test@example.com", "999999999"
        ));
    }
}