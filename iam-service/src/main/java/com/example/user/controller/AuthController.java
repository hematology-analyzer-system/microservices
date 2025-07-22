//package com.example.user.controller;
//
//import com.example.user.dto.auth.AuthRequest;
//import com.example.user.dto.auth.ResendEmailRequest;
//import com.example.user.dto.auth.VerifyOtpRequest;
//import com.example.user.dto.register.RegisterRequest;
//import com.example.user.dto.auth.ResetPasswordRequest; // Import the new DTO
//import com.example.user.model.*;
//import com.example.user.repository.UserRepository;
//import com.example.user.repository.VerificationTokenRepository;
//import com.example.user.security.JwtService;
//import com.example.user.service.EmailService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid; // For @Valid annotation
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//@Slf4j
//@EnableTransactionManagement
//public class AuthController {
//
//    private final AuthenticationManager authManager;
//    private final JwtService jwtService;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private VerificationTokenRepository verificationTokenRepository;
//
//    private static final int OTP_LENGTH = 6;
//    private static final int OTP_VALIDITY_MINUTES = 5;
//    // Removed USER_UNVERIFIED_MINUTES as it's not directly used here for logic
//
//    @PostMapping("/me")
//    public ResponseEntity<?> me(@RequestBody AuthRequest request) {
//        Authentication authentication = authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//        );
//
//        Optional<User> userOptional = userRepository.findByEmail(request.getUsername());
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//
//        User user = userOptional.get();
//        // Build claims
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("status", user.getStatus());
//
//        Set<Map<String, Object>> userRoles = new HashSet<>();
//        Set<Long> privilegeIds = new HashSet<>();
//
//        if (user.getRoles() != null) {
//            for (Role role : user.getRoles()) {
//                Map<String, Object> roleInfo = new HashMap<>();
//                roleInfo.put("id", role.getRoleId());
//                roleInfo.put("name", role.getName());
//                roleInfo.put("code", role.getCode());
//                userRoles.add(roleInfo);
//
//                if (role.getPrivileges() != null) {
//                    for (Privilege privilege : role.getPrivileges()) {
//                        privilegeIds.add(privilege.getPrivilegeId());
//                    }
//                }
//            }
//        }
//
//        claims.put("roles", userRoles);
//        claims.put("privilege_ids", privilegeIds);
//
//        return ResponseEntity.ok(claims);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
//        try {
//            log.info("Login attempt for user: {}", request.getUsername());
//
//            // Authenticate credentials
//            Authentication authentication = authManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//            );
//
//            log.info("Authentication successful for user: {}", request.getUsername());
//
//            // Fetch user details
//            var userOptional = userRepository.findByEmail(request.getUsername());
//            if (userOptional.isEmpty()) {
//                log.error("User not found: {}", request.getUsername());
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Collections.singletonMap("error", "User not found"));
//            }
//
//            var user = userOptional.get();
//
//            // Check if user account is active before allowing login
//            if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
//                log.warn("Login failed: User {} account is not active (status: {})", user.getEmail(), user.getStatus());
//                return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden for non-active users
//                        .body(Collections.singletonMap("error", "Account is not active. Please verify your email or contact support."));
//            }
//
//            // Generate JWT
//            var token = jwtService.generateToken(user);
//            log.info("JWT token generated for user: {}", request.getUsername());
//
//            // Set JWT as HttpOnly cookie
//            ResponseCookie cookie = ResponseCookie.from("token", token)
//                    .httpOnly(true)
//                    .secure(false) // Set to true in production with HTTPS
//                    .path("/")
//                    .maxAge(Duration.ofDays(1))
//                    .sameSite("Lax") // Can be "Strict" for stronger protection
//                    .build();
//
//            // Set cookie in header
//            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//
//            // Return response body (optional, can omit token here for security)
//            Map<String, Object> body = new HashMap<>();
//            body.put("message", "Login successful");
//            body.put("username", user.getUsername());
//            body.put("email", user.getEmail()); // Include email
//            body.put("status", user.getStatus()); // Include status
//            body.put("token", token);
//            body.put("expiresIn", 86400); // Optional
//
//            return ResponseEntity.ok(body);
//
//        } catch (BadCredentialsException e) {
//            log.error("Invalid credentials for user: {}", request.getUsername());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Collections.singletonMap("error", "Invalid credentials"));
//        } catch (AuthenticationException e) { // Catch broader Spring Security authentication errors
//            log.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Collections.singletonMap("error", "Authentication failed: " + e.getMessage()));
//        } catch (Exception e) {
//            log.error("Login error: {}", e.getMessage(), e); // Log full stack trace
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "Login failed due to an unexpected error."));
//        }
//    }
//
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) { // Add @Valid
//        List<String> duplicateFields = new ArrayList<>();
//
//        if (userRepository.existsByEmail(request.getEmail())) {
//            duplicateFields.add("email");
//            log.info("Registration failed: Email {} already exists", request.getEmail());
//        }
//
//        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
//            duplicateFields.add("phone");
//            log.info("Registration failed: Phone {} already exists", request.getPhone());
//        }
//
//        if (request.getIdentifyNum() != null && userRepository.existsByIdentifyNum(request.getIdentifyNum())) {
//            duplicateFields.add("identify");
//            log.info("Registration failed: Identify Number {} already exists", request.getIdentifyNum());
//        }
//
//        if (!duplicateFields.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", duplicateFields));
//        }
//
//        User user = new User();
//        user.setFullName(request.getFullName());
//        user.setEmail(request.getEmail());
//        user.setPhone(request.getPhone());
//        user.setGender(request.getGender());
//        user.setDate_of_Birth(request.getDate_of_Birth());
//        user.setAddress(request.getAddress());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setStatus(User.status.PENDING_VERIFICATION.toString()); // Set status to PENDING_VERIFICATION
//        user.setIdentifyNum(request.getIdentifyNum());
//
//        // Set audit info before saving for the first time
//        user.setCreatedBy(new UserAuditInfo(
//                null, // userId is not yet available here
//                request.getFullName(),
//                request.getEmail(),
//                request.getIdentifyNum()
//        ));
//        user.setUpdatedBy(new UserAuditInfo(
//                null, // userId is not yet available here
//                request.getFullName(),
//                request.getEmail(),
//                request.getIdentifyNum()
//        ));
//        userRepository.save(user); // First save to get the user ID
//
//        // Now update audit info with the generated user ID and save again
//        user.setCreatedBy(new UserAuditInfo(
//                user.getId(),
//                user.getFullName(),
//                user.getEmail(),
//                user.getIdentifyNum()
//        ));
//        user.setUpdatedBy(new UserAuditInfo(
//                user.getId(),
//                user.getFullName(), // Use user's full name, not request's
//                user.getEmail(),
//                user.getIdentifyNum()
//        ));
//        userRepository.save(user); // Second save to persist audit info with user ID
//
//        sendVerificationOtp(user, "registration"); // Indicate purpose of OTP
//
//        log.info("User {} registered successfully with PENDING_VERIFICATION status.", user.getEmail());
//        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully! Please check your email to verify your account."));
//    }
//
//
//    @PostMapping("/resend-otp")
//    public ResponseEntity<?> resendOtp(@RequestBody ResendEmailRequest request) { // Expect email in request body
//        String email = request.getEmail();
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
//        }
//
//        User user = userOptional.get();
//        // This endpoint is primarily for re-sending registration OTPs for PENDING_VERIFICATION users
//        if (!user.getStatus().equals(User.status.PENDING_VERIFICATION.toString())) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not in pending verification state."));
//        }
//
//        log.info(user.getEmail());
//
//        sendVerificationOtp(user, "registration"); // Resend OTP for registration
//        log.info("Resent registration OTP to {}", user.getEmail());
//        return ResponseEntity.ok(Collections.singletonMap("message", "New OTP sent successfully."));
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) { // Add @Valid
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
//        }
//
//        User user = userOptional.get();
//
//        // This endpoint specifically handles activating users who are PENDING_VERIFICATION (registration flow)
//        if (!user.getStatus().equals(User.status.PENDING_VERIFICATION.toString())) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not awaiting verification or already active."));
//        }
//
//        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());
//
//        if (tokenOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
//        }
//
//        VerificationToken token = tokenOptional.get();
//
//        if (!token.getUser().getId().equals(user.getId())) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
//        }
//
//        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
//            // Delete expired token
//            verificationTokenRepository.delete(token);
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
//        }
//
//        // OTP is valid, activate user
//        user.setStatus(User.status.ACTIVE.toString());
//        userRepository.save(user);
//
//        // Delete the used token
//        verificationTokenRepository.delete(token);
//
//        log.info("User {} email verified successfully and status set to ACTIVE.", user.getEmail());
//        return ResponseEntity.ok(Collections.singletonMap("message", "Email verified successfully!"));
//    }
//
//    // --- NEW ENDPOINTS FOR FORGOT PASSWORD FLOW ---
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody String email) { // Expect email as plain string
//        log.info("Forgot password request for email: {}", email);
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if (userOptional.isEmpty()) {
//            // For security, always return a generic success message
//            log.warn("Forgot password request for non-existent or inactive email: {}", email);
//            return ResponseEntity.ok(Collections.singletonMap("message", "If an account with that email exists, a password reset OTP has been sent."));
//        }
//
//        User user = userOptional.get();
//        // Only allow password reset for ACTIVE users
//        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
//            log.warn("Forgot password request for non-active user: {}", user.getEmail());
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account is not active. Please verify your email first."));
//        }
//
//        // Generate and send OTP for password reset purpose
//        sendVerificationOtp(user, "password_reset");
//        log.info("Password reset OTP sent to {}.", user.getEmail());
//        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset OTP sent to your email."));
//    }
//
//    @PostMapping("/verify-reset-otp")
//    public ResponseEntity<?> verifyResetOtp(@Valid @RequestBody VerifyOtpRequest request) { // Add @Valid
//        log.info("Verify reset OTP request for email: {}", request.getEmail());
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
//        }
//
//        User user = userOptional.get();
//        // Ensure the user is active before allowing password reset verification
//        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not active or is pending verification."));
//        }
//
//        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());
//
//        if (tokenOptional.isEmpty()) {
//            log.warn("Invalid OTP attempted for email: {}", request.getEmail());
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
//        }
//
//        VerificationToken token = tokenOptional.get();
//
//        if (!token.getUser().getId().equals(user.getId())) {
//            log.warn("OTP {} does not match user {} for reset.", request.getOtp(), request.getEmail());
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
//        }
//
//        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
//            // Delete expired token
//            verificationTokenRepository.delete(token);
//            log.warn("Expired OTP for email: {}", request.getEmail());
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
//        }
//
//        // OTP is valid for password reset. Delete the token.
//        // The user's status remains ACTIVE. Frontend will proceed to reset password.
//        verificationTokenRepository.delete(token);
//        log.info("Password reset OTP verified for user {}.", user.getEmail());
//        return ResponseEntity.ok(Collections.singletonMap("message", "OTP verified. You can now reset your password."));
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) { // Add @Valid
//        log.info("Reset password request for email: {}", request.getEmail());
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
//        }
//
//        User user = userOptional.get();
//
//        // In a more secure flow, you might want to ensure a temporary "reset token"
//        // was just verified. For this setup, we rely on the frontend flow.
//        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not active."));
//        }
//
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        userRepository.save(user);
//
//        log.info("Password successfully reset for user {}.", user.getEmail());
//        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully."));
//    }
//
//    // --- HELPER METHODS ---
//
//    // Modified to accept a 'purpose' string for email customization
//
//    @Transactional
//    private void sendVerificationOtp(User user, String purpose) {
//        // Delete any existing tokens for this user to ensure only one active OTP
//        verificationTokenRepository.deleteByUser(user);
//
//        String otp = generateOtp();
//        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);
//
//        VerificationToken verificationToken = new VerificationToken(otp, user, expiryDate);
//        verificationTokenRepository.save(verificationToken);
//
//        String subject;
//        String emailBody;
//
//        if ("registration".equalsIgnoreCase(purpose)) {
//            subject = "Verify Your Email - OTP for Registration";
//            emailBody = "Dear " + user.getFullName() + ",\n\n"
//                    + "Your One-Time Password (OTP) for registration is: " + otp + "\n"
//                    + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n"
//                    + "If you did not request this, please ignore this email.\n\n"
//                    + "Regards,\nYour App Team";
//        } else if ("password_reset".equalsIgnoreCase(purpose)) {
//            subject = "Password Reset OTP";
//            emailBody = "Dear " + user.getFullName() + ",\n\n"
//                    + "Your One-Time Password (OTP) for password reset is: " + otp + "\n"
//                    + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n"
//                    + "Please use this code to reset your password. Do not share it with anyone.\n\n"
//                    + "If you did not request a password reset, please ignore this email.\n\n"
//                    + "Regards,\nYour App Team";
//        } else {
//            // Default/fallback for unknown purpose
//            subject = "Your OTP";
//            emailBody = "Dear " + user.getFullName() + ",\n\n"
//                    + "Your One-Time Password (OTP) is: " + otp + "\n"
//                    + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n"
//                    + "Regards,\nYour App Team";
//        }
//
//        emailService.sendEmail(user.getEmail(), subject, emailBody);
//        log.info("OTP {} (Purpose: {}) sent to {}", otp, purpose, user.getEmail());
//    }
//
//    private String generateOtp() {
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//        for (int i = 0; i < OTP_LENGTH; i++) {
//            otp.append(random.nextInt(10)); // Generates a digit from 0-9
//        }
//        return otp.toString();
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletResponse response) {
//        ResponseCookie cookie = ResponseCookie.from("token", "")
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .maxAge(0)
//                .build();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(Map.of("message", "Logged out"));
//    }
//}

package com.example.user.controller;

import com.example.user.dto.auth.AuthRequest;
import com.example.user.dto.auth.ResendEmailRequest;
import com.example.user.dto.auth.VerifyOtpRequest;
import com.example.user.dto.register.RegisterRequest;
import com.example.user.dto.auth.ResetPasswordRequest; // Import the new DTO
import com.example.user.model.*;
import com.example.user.repository.UserRepository;
import com.example.user.repository.VerificationTokenRepository;
import com.example.user.security.JwtService;
import com.example.user.service.EmailService;
// import jakarta.transaction.Transactional; // REMOVE this import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional; // Keep this import for Spring's @Transactional
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid; // For @Valid annotation

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@EnableTransactionManagement
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;
    // Removed USER_UNVERIFIED_MINUTES as it's not directly used here for logic

    @PostMapping("/me")
    public ResponseEntity<?> me(@RequestBody AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Optional<User> userOptional = userRepository.findByEmail(request.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        // Build claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("status", user.getStatus());

        Set<Map<String, Object>> userRoles = new HashSet<>();
        Set<Long> privilegeIds = new HashSet<>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                Map<String, Object> roleInfo = new HashMap<>();
                roleInfo.put("id", role.getRoleId());
                roleInfo.put("name", role.getName());
                roleInfo.put("code", role.getCode());
                userRoles.add(roleInfo);

                if (role.getPrivileges() != null) {
                    for (Privilege privilege : role.getPrivileges()) {
                        privilegeIds.add(privilege.getPrivilegeId());
                    }
                }
            }
        }

        claims.put("roles", userRoles);
        claims.put("privilege_ids", privilegeIds);

        return ResponseEntity.ok(claims);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            // Authenticate credentials
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            log.info("Authentication successful for user: {}", request.getUsername());

            // Fetch user details
            var userOptional = userRepository.findByEmail(request.getUsername());
            if (userOptional.isEmpty()) {
                log.error("User not found: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            var user = userOptional.get();

            // Check if user account is active before allowing login
            if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
                log.warn("Login failed: User {} account is not active (status: {})", user.getEmail(), user.getStatus());
                return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden for non-active users
                        .body(Collections.singletonMap("error", "Account is not active. Please verify your email or contact support."));
            }

            // Generate JWT
            var token = jwtService.generateToken(user);
            log.info("JWT token generated for user: {}", request.getUsername());

            // Set JWT as HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false) // Set to true in production with HTTPS
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Lax") // Can be "Strict" for stronger protection
                    .build();

            // Set cookie in header
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Return response body (optional, can omit token here for security)
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Login successful");
            body.put("username", user.getUsername());
            body.put("email", user.getEmail()); // Include email
            body.put("status", user.getStatus()); // Include status
            body.put("token", token);
            body.put("expiresIn", 86400); // Optional

            return ResponseEntity.ok(body);

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
        } catch (AuthenticationException e) { // Catch broader Spring Security authentication errors
            log.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Authentication failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e); // Log full stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Login failed due to an unexpected error."));
        }
    }


    @PostMapping("/register")
    @Transactional // Added @Transactional here because this method performs multiple database writes (two saves and then a call to sendVerificationOtp which also modifies the DB).
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) { // Add @Valid
        List<String> duplicateFields = new ArrayList<>();

        if (userRepository.existsByEmail(request.getEmail())) {
            duplicateFields.add("email");
            log.info("Registration failed: Email {} already exists", request.getEmail());
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            duplicateFields.add("phone");
            log.info("Registration failed: Phone {} already exists", request.getPhone());
        }

        if (request.getIdentifyNum() != null && userRepository.existsByIdentifyNum(request.getIdentifyNum())) {
            duplicateFields.add("identify");
            log.info("Registration failed: Identify Number {} already exists", request.getIdentifyNum());
        }

        if (!duplicateFields.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", duplicateFields));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDate_of_Birth(request.getDate_of_Birth());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(User.status.PENDING_VERIFICATION.toString()); // Set status to PENDING_VERIFICATION
        user.setIdentifyNum(request.getIdentifyNum());

        // Set audit info before saving for the first time
        user.setCreatedBy(new UserAuditInfo(
                null, // userId is not yet available here
                request.getFullName(),
                request.getEmail(),
                request.getIdentifyNum()
        ));
        user.setUpdatedBy(new UserAuditInfo(
                null, // userId is not yet available here
                request.getFullName(),
                request.getEmail(),
                request.getIdentifyNum()
        ));
        userRepository.save(user); // First save to get the user ID

        // Now update audit info with the generated user ID and save again
        user.setCreatedBy(new UserAuditInfo(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getIdentifyNum()
        ));
        user.setUpdatedBy(new UserAuditInfo(
                user.getId(),
                user.getFullName(), // Use user's full name, not request's
                user.getEmail(),
                user.getIdentifyNum()
        ));
        userRepository.save(user); // Second save to persist audit info with user ID

        sendVerificationOtp(user, "registration"); // Indicate purpose of OTP

        log.info("User {} registered successfully with PENDING_VERIFICATION status.", user.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully! Please check your email to verify your account."));
    }


    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ResendEmailRequest request) { // Expect email in request body
        String email = request.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();
        // This endpoint is primarily for re-sending registration OTPs for PENDING_VERIFICATION users
        if (!user.getStatus().equals(User.status.PENDING_VERIFICATION.toString())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not in pending verification state."));
        }

        log.info(user.getEmail());

        sendVerificationOtp(user, "registration"); // Resend OTP for registration
        log.info("Resent registration OTP to {}", user.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "New OTP sent successfully."));
    }

    @PostMapping("/verify-otp")
    @Transactional // Added @Transactional here as this method performs user.save and token deletion.
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) { // Add @Valid
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();

        // This endpoint specifically handles activating users who are PENDING_VERIFICATION (registration flow)
        if (!user.getStatus().equals(User.status.PENDING_VERIFICATION.toString())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not awaiting verification or already active."));
        }

        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
        }

        VerificationToken token = tokenOptional.get();

        if (!token.getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Delete expired token
            verificationTokenRepository.delete(token);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
        }

        // OTP is valid, activate user
        user.setStatus(User.status.ACTIVE.toString());
        userRepository.save(user);

        // Delete the used token
        verificationTokenRepository.delete(token);

        log.info("User {} email verified successfully and status set to ACTIVE.", user.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "Email verified successfully!"));
    }

    // --- NEW ENDPOINTS FOR FORGOT PASSWORD FLOW ---

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) { // Expect email as plain string
        log.info("Forgot password request for email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // For security, always return a generic success message
            log.warn("Forgot password request for non-existent or inactive email: {}", email);
            return ResponseEntity.ok(Collections.singletonMap("message", "If an account with that email exists, a password reset OTP has been sent."));
        }

        User user = userOptional.get();
        // Only allow password reset for ACTIVE users
        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
            log.warn("Forgot password request for non-active user: {}", user.getEmail());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account is not active. Please verify your email first."));
        }

        // Generate and send OTP for password reset purpose
        sendVerificationOtp(user, "password_reset");
        log.info("Password reset OTP sent to {}.", user.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset OTP sent to your email."));
    }

    @PostMapping("/verify-reset-otp")
    @Transactional // Added @Transactional here as this method performs token deletion.
    public ResponseEntity<?> verifyResetOtp(@Valid @RequestBody VerifyOtpRequest request) { // Add @Valid
        log.info("Verify reset OTP request for email: {}", request.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();
        // Ensure the user is active before allowing password reset verification
        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not active or is pending verification."));
        }

        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());

        if (tokenOptional.isEmpty()) {
            log.warn("Invalid OTP attempted for email: {}", request.getEmail());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
        }

        VerificationToken token = tokenOptional.get();

        if (!token.getUser().getId().equals(user.getId())) {
            log.warn("OTP {} does not match user {} for reset.", request.getOtp(), request.getEmail());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Delete expired token
            verificationTokenRepository.delete(token);
            log.warn("Expired OTP for email: {}", request.getEmail());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
        }

        // OTP is valid for password reset. Delete the token.
        // The user's status remains ACTIVE. Frontend will proceed to reset password.
        verificationTokenRepository.delete(token);
        log.info("Password reset OTP verified for user {}.", user.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "OTP verified. You can now reset your password."));
    }

    @PostMapping("/reset-password")
    @Transactional // Added @Transactional here as this method performs user.save
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) { // Add @Valid
        log.info("Reset password request for email: {}", request.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();

        // In a more secure flow, you might want to ensure a temporary "reset token"
        // was just verified. For this setup, we rely on the frontend flow.
        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not active."));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password successfully reset for user {}.", user.getEmail());
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully."));
    }

    // --- HELPER METHODS ---

    // Modified to accept a 'purpose' string for email customization

    @Transactional // This was the primary missing @Transactional from the previous conversation.
    protected void sendVerificationOtp(User user, String purpose) {
        // Delete any existing tokens for this user to ensure only one active OTP
        verificationTokenRepository.deleteByUser(user);

        String otp = generateOtp();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);

        VerificationToken verificationToken = new VerificationToken(otp, user, expiryDate);
        verificationTokenRepository.save(verificationToken);

        String subject;
        String emailBody;

        if ("registration".equalsIgnoreCase(purpose)) {
            subject = "Verify Your Email - OTP for Registration";
            emailBody = "Dear " + user.getFullName() + ",\n\n"
                    + "Your One-Time Password (OTP) for registration is: " + otp + "\n"
                    + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Regards,\nYour App Team";
        } else if ("password_reset".equalsIgnoreCase(purpose)) {
            subject = "Password Reset OTP";
            emailBody = "Dear " + user.getFullName() + ",\n\n"
                    + "Your One-Time Password (OTP) for password reset is: " + otp + "\n"
                    + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n"
                    + "Please use this code to reset your password. Do not share it with anyone.\n\n"
                    + "If you did not request a password reset, please ignore this email.\n\n"
                    + "Regards,\nYour App Team";
        } else {
            // Default/fallback for unknown purpose
            subject = "Your OTP";
            emailBody = "Dear " + user.getFullName() + ",\n\n"
                    + "Your One-Time Password (OTP) is: " + otp + "\n"
                    + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n"
                    + "Regards,\nYour App Team";
        }

        emailService.sendEmail(user.getEmail(), subject, emailBody);
        log.info("OTP {} (Purpose: {}) sent to {}", otp, purpose, user.getEmail());
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Generates a digit from 0-9
        }
        return otp.toString();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out"));
    }
}