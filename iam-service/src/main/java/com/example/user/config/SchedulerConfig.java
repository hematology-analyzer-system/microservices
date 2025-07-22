package com.example.user.config;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private static final long TEN_MINUTES_IN_MS = 10 * 60 * 1000; // 10 minutes in milliseconds

    // This method will run every 1 minute
    @Scheduled(fixedRate = 60000) // Run every 60 seconds (1 minute)
    @Transactional // Ensure transactional for deletion operations
    public void cleanupUnverifiedUsersAndTokens() {
        log.info("Running scheduled cleanup for unverified users and expired tokens.");
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5); // For expired tokens

        // Delete expired verification tokens (older than 5 minutes)
        verificationTokenRepository.deleteByExpiryDateBefore(fiveMinutesAgo);
        log.info("Cleaned up expired verification tokens.");


        // Find users that are PENDING_VERIFICATION and created more than 10 minutes ago
        List<User> unverifiedUsers = userRepository.findByStatusAndCreatedAtBefore(User.status.PENDING_VERIFICATION.toString(), tenMinutesAgo);

        for (User user : unverifiedUsers) {
            // Delete associated verification tokens first to avoid foreign key constraints
            verificationTokenRepository.deleteByUser(user);
            userRepository.delete(user);
            log.info("Deleted unverified user: {}", user.getEmail());
        }
        log.info("Finished cleanup. Deleted {} unverified users.", unverifiedUsers.size());
    }
}
