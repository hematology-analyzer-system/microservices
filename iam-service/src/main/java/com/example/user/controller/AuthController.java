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
import com.example.user.service.UserService;
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
    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    UserAuditLog auditLog = new UserAuditLog();

    String defaultMalePic = "/images/defaultMale.png";
    String defaultFemalePic = "/images/defaultFemale.png";

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;
    @Autowired
    private UserService userService;
    // Removed USER_UNVERIFIED_MINUTES as it's not directly used here for logic

    @PostMapping("/me")
    public ResponseEntity<?> me(@RequestBody AuthRequest request) {
        // Authenticate the user
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Optional<User> userOptional = userRepository.findByEmail(request.getUsername());

        if (userOptional.isEmpty()) {
            // This case should ideally not be reached if authentication succeeded,
            // but it's good for defensive programming.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();

        // Prepare the response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", user.getEmail());
        responseData.put("fullName", user.getFullName()); // Added fullName

        Set<Long> privilegeIds = new HashSet<>();

        // Collect all unique privilege IDs from all roles of the user
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getPrivileges() != null) {
                    for (Privilege privilege : role.getPrivileges()) {
                        privilegeIds.add(privilege.getPrivilegeId());
                    }
                }
            }
        }

        responseData.put("privilege_ids", privilegeIds); // Added privilege_ids

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
       

        try {
            log.info("Login attempt for user: {}", request.getUsername());

            auditLog.setUserId(null);
            auditLog.setUserName(request.getUsername());
            auditLog.setFullName(null);
            auditLog.setEmail(null);
            auditLog.setIdentifyNum(null);
            auditLog.setDetails("Login attempt for user: " + request.getUsername());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            log.info("Authentication successful for user: {}", request.getUsername());
            auditLog.setDetails("Authentication successful for user: " + request.getUsername());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);
            var userOptional = userRepository.findByEmail(request.getUsername());
            if (userOptional.isEmpty()) {
                log.error("User not found: {}", request.getUsername());
                auditLog.setDetails("User not found: " + request.getUsername());
                rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            var user = userOptional.get();

            if (!user.getStatus().equals(User.status.ACTIVE.toString()) && !user.getStatus().equals(User.status.CHANGING.toString())) {
                log.warn("Login failed: User {} account is not active (status: {})", user.getEmail(), user.getStatus());
                auditLog.setUserId(user.getId());
                auditLog.setFullName(user.getFullName());
                auditLog.setEmail(user.getEmail());
                auditLog.setIdentifyNum(user.getIdentifyNum());
                auditLog.setDetails("Login failed: User " + user.getEmail() + " account is not active");
                rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);
                return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden for non-active users
                        .body(Collections.singletonMap("error", "Account is not active. Please verify your email or contact support."));
            }
            user.setStatus("ACTIVE");
            userRepository.save(user);
            // Generate JWT
            var token = jwtService.generateToken(user);
            log.info("JWT token generated for user: {}", request.getUsername());
            auditLog.setUserId(user.getId());
            auditLog.setUserName(user.getUsername());
            auditLog.setFullName(user.getFullName());
            auditLog.setEmail(user.getEmail());
            auditLog.setIdentifyNum(user.getIdentifyNum());
            auditLog.setDetails("JWT token generated for user: " + request.getUsername());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);

            // Set JWT as HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true) // Set to true in production with HTTPS
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

            // Send RabbitMQ message after successful login
            auditLog.setUserId(user.getId());
            auditLog.setUserName(user.getUsername());
            auditLog.setFullName(user.getFullName());
            auditLog.setEmail(user.getEmail());
            auditLog.setIdentifyNum(user.getIdentifyNum());
            auditLog.setDetails("User logged in: " + user.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);

            return ResponseEntity.ok(body);

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            auditLog.setDetails("Invalid credentials for user: " + request.getUsername());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
        } catch (AuthenticationException e) { // Catch broader Spring Security authentication errors
            log.error("Authentication error for user {}: {}", request.getUsername(), e.getMessage());
            auditLog.setDetails("Authentication error for user " + request.getUsername() + ": " + e.getMessage());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Authentication failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e); // Log full stack trace
            auditLog.setDetails("Login error: " + e.getMessage());
            rabbitTemplate.convertAndSend("appExchange", "login.key", auditLog);
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
            auditLog.setUserId(null);
            auditLog.setFullName(null);
            auditLog.setEmail(null);
            auditLog.setIdentifyNum(null);
            auditLog.setDetails("Registration failed: Email " + request.getEmail() + " already exists");
            rabbitTemplate.convertAndSend("appExchange", "register.key", auditLog); // Send
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            duplicateFields.add("phone");
            log.info("Registration failed: Phone {} already exists", request.getPhone());
            auditLog.setUserId(null);
            auditLog.setFullName(null);
            auditLog.setEmail(null);
            auditLog.setIdentifyNum(null);
            auditLog.setDetails("Registration failed: Phone " + request.getPhone() + " already exists");
            rabbitTemplate.convertAndSend("appExchange", "register.key", auditLog); // Send
        }

        if (request.getIdentifyNum() != null && userRepository.existsByIdentifyNum(request.getIdentifyNum())) {
            duplicateFields.add("identify");
            log.info("Registration failed: Identify Number {} already exists", request.getIdentifyNum());
            auditLog.setUserId(null);
            auditLog.setFullName(null);
            auditLog.setEmail(null);
            auditLog.setIdentifyNum(request.getIdentifyNum());
            auditLog.setDetails("Registration failed: Identify Number " + request.getIdentifyNum() + " already exists");
            rabbitTemplate.convertAndSend("appExchange", "register.key", auditLog); // Send
        }

        if (!duplicateFields.isEmpty()) {
            auditLog.setDetails("Registration failed due to duplicate fields: " + String.join(", ", duplicateFields));
            auditLog.setUserId(null);
            auditLog.setFullName(null);
            auditLog.setEmail(null);
            auditLog.setIdentifyNum(null);
            rabbitTemplate.convertAndSend("appExchange", "register.key", auditLog); // Send
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

        String gender = request.getGender();
        if ("female".equalsIgnoreCase(gender)) {
            user.setProfilePic(defaultFemalePic);
        } else {
            user.setProfilePic(defaultMalePic);
        }

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

        List<Long> defaultRole = new ArrayList<>();
        defaultRole.add(1L);
        userService.assignRoleToUser(user.getId(), defaultRole);

        userRepository.save(user); // Second save to persist audit info with user ID

        sendVerificationOtp(user, "registration"); // Indicate purpose of OTP

        log.info("User {} registered successfully with PENDING_VERIFICATION status.", user.getEmail());

        // Send RabbitMQ message after registration
        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setEmail(user.getEmail());
        auditLog.setIdentifyNum(user.getIdentifyNum());
        auditLog.setDetails("User registered: " + user.getEmail() + " with PENDING_VERIFICATION status.");
        rabbitTemplate.convertAndSend("appExchange", "register.key", auditLog);
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
        // Send RabbitMQ message after successful resend-otp
        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setEmail(user.getEmail());
        auditLog.setIdentifyNum(user.getIdentifyNum());
        auditLog.setDetails("Resent registration OTP to: " + user.getEmail());
        rabbitTemplate.convertAndSend("appExchange", "resendotp.key", auditLog);
        return ResponseEntity.ok(Collections.singletonMap("message", "New OTP sent successfully."));
    }

    @PostMapping("/verify-otp")
    @Transactional // Added @Transactional here as this method performs user.save and token deletion.
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) { // Add @Valid
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            auditLog.setDetails("User not found: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();

        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setEmail(user.getEmail());
        auditLog.setIdentifyNum(user.getIdentifyNum());

        // This endpoint specifically handles activating users who are PENDING_VERIFICATION (registration flow)
        if (!user.getStatus().equals(User.status.PENDING_VERIFICATION.toString())) {
            auditLog.setDetails("User " + user.getEmail() + " is not awaiting verification or already active.");
            rabbitTemplate.convertAndSend("appExchange", "verifyotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not awaiting verification or already active."));
        }

        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());

        if (tokenOptional.isEmpty()) {
            auditLog.setDetails("Invalid OTP attempted for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
        }

        VerificationToken token = tokenOptional.get();

        if (!token.getUser().getId().equals(user.getId())) {
            auditLog.setDetails("OTP " + request.getOtp() + " does not match user " + request.getEmail() + " for verification.");
            rabbitTemplate.convertAndSend("appExchange", "verifyotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Delete expired token
            verificationTokenRepository.delete(token);
            auditLog.setDetails("Expired OTP for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
        }

        // OTP is valid, activate user
        user.setStatus(User.status.ACTIVE.toString());
        userRepository.save(user);

        // Delete the used token
        verificationTokenRepository.delete(token);

        log.info("User {} email verified successfully and status set to ACTIVE.", user.getEmail());
        // Send RabbitMQ message after successful email verification
        auditLog.setDetails("User " + user.getEmail() + " email verified successfully and status set to ACTIVE.");
        rabbitTemplate.convertAndSend("appExchange", "verifyotp.key", auditLog);
        return ResponseEntity.ok(Collections.singletonMap("message", "Email verified successfully!"));
    }

    // --- NEW ENDPOINTS FOR FORGOT PASSWORD FLOW ---

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) { // Expect email as plain string
        log.info("Forgot password request for email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        auditLog.setEmail(email);

        if (userOptional.isEmpty()) {
            // For security, always return a generic success message
            log.warn("Forgot password request for non-existent or inactive email: {}", email);
            auditLog.setDetails("Forgot password request for non-existent or inactive email: " + email);
            rabbitTemplate.convertAndSend("appExchange", "forgotpassword.key", auditLog);
            return ResponseEntity.ok(Collections.singletonMap("message", "If an account with that email exists, a password reset OTP has been sent."));
        }

        User user = userOptional.get();

        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setIdentifyNum(user.getIdentifyNum());


        // Only allow password reset for ACTIVE users
        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
            log.warn("Forgot password request for non-active user: {}", user.getEmail());
            auditLog.setDetails("Forgot password request for non-active user: " + user.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "forgotpassword.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account is not active. Please verify your email first."));
        }

        // Generate and send OTP for password reset purpose
        sendVerificationOtp(user, "password_reset");
        log.info("Password reset OTP sent to {}.", user.getEmail());
        auditLog.setDetails("Password reset OTP sent to: " + user.getEmail());
        rabbitTemplate.convertAndSend("appExchange", "forgotpassword.key", auditLog);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset OTP sent to your email."));
    }

    @PostMapping("/verify-reset-otp")
    @Transactional // Added @Transactional here as this method performs token deletion.
    public ResponseEntity<?> verifyResetOtp(@Valid @RequestBody VerifyOtpRequest request) { // Add @Valid
        log.info("Verify reset OTP request for email: {}", request.getEmail());
        auditLog.setEmail(request.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            auditLog.setDetails("User not found for reset OTP: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyresetotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();
        // Ensure the user is active before allowing password reset verification
        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
            auditLog.setDetails("Account not active or is pending verification for user: " + user.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyresetotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not active or is pending verification."));
        }

        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());

        if (tokenOptional.isEmpty()) {
            log.warn("Invalid OTP attempted for email: {}", request.getEmail());
            auditLog.setDetails("Invalid OTP attempted for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyresetotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
        }

        VerificationToken token = tokenOptional.get();

        if (!token.getUser().getId().equals(user.getId())) {
            log.warn("OTP {} does not match user {} for reset.", request.getOtp(), request.getEmail());
            auditLog.setDetails("OTP " + request.getOtp() + " does not match user " + request.getEmail() + " for reset.");
            rabbitTemplate.convertAndSend("appExchange", "verifyresetotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Delete expired token
            verificationTokenRepository.delete(token);
            log.warn("Expired OTP for email: {}", request.getEmail());
            auditLog.setDetails("Expired OTP for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyresetotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
        }

        // OTP is valid for password reset. Delete the token.
        // The user's status remains ACTIVE. Frontend will proceed to reset password.
        verificationTokenRepository.delete(token);
        log.info("Password reset OTP verified for user {}.", user.getEmail());
        auditLog.setDetails("Password reset OTP verified for user: " + user.getEmail());
        rabbitTemplate.convertAndSend("appExchange", "verifyresetotp.key", auditLog);

        return ResponseEntity.ok(Collections.singletonMap("message", "OTP verified. You can now reset your password."));
    }

    @PostMapping("/reset-password")
    @Transactional // Added @Transactional here as this method performs user.save
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) { // Add @Valid
        log.info("Reset password request for email: {}", request.getEmail());

        
        auditLog.setDetails("Reset password request for email: " + request.getEmail());
        rabbitTemplate.convertAndSend("appExchange", "resetpassword.key", auditLog);

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            auditLog.setDetails("User not found for reset password: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "resetpassword.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();

        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setEmail(user.getEmail());
        auditLog.setIdentifyNum(user.getIdentifyNum());

        // In a more secure flow, you might want to ensure a temporary "reset token"
        // was just verified. For this setup, we rely on the frontend flow.
        if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
            auditLog.setDetails("Password reset request failed for user: " + user.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "resetpassword.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Account not active."));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password successfully reset for user {}.", user.getEmail());
        auditLog.setDetails("Password successfully reset for user: " + user.getEmail());
        rabbitTemplate.convertAndSend("appExchange", "resetpassword.key", auditLog);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully."));
    }

    @PostMapping("/verify-activation-otp")
    @Transactional
    public ResponseEntity<?> verifyActivationOtp(@Valid @RequestBody VerifyOtpRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        auditLog.setEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            auditLog.setDetails("Activation OTP verification failed: User not found for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyactivationotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
        }

        User user = userOptional.get();

        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setIdentifyNum(user.getIdentifyNum());
        auditLog.setEmail(user.getEmail());

        // Only allow activation if status is PENDING_ACTIVATION
        if (!user.getStatus().equals(User.status.PENDING_ACTIVATION.toString())) {
            auditLog.setDetails("Activation OTP verification failed: User " + user.getEmail() + " is not in activation state.");
            rabbitTemplate.convertAndSend("appExchange", "verifyactivationotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not in activation state."));
        }

        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(request.getOtp());

        if (tokenOptional.isEmpty()) {
            auditLog.setDetails("Activation OTP verification failed: Invalid OTP for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyactivationotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP."));
        }

        VerificationToken token = tokenOptional.get();

        if (!token.getUser().getId().equals(user.getId())) {
            auditLog.setDetails("Activation OTP verification failed: Invalid OTP for user " + user.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyactivationotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid OTP for this user."));
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            auditLog.setDetails("Activation OTP verification failed: OTP expired for email: " + request.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "verifyactivationotp.key", auditLog);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "OTP has expired. Please request a new one."));
        }

        // OTP valid — Activate the user
        user.setStatus(User.status.ACTIVE.toString());
        userRepository.save(user);
        verificationTokenRepository.delete(token);

        log.info("User {} activated successfully via admin OTP.", user.getEmail());
        auditLog.setDetails("User activated successfully via admin OTP: " + user.getEmail());
        rabbitTemplate.convertAndSend("appExchange", "verifyactivationotp.key", auditLog);
        return ResponseEntity.ok(Collections.singletonMap("message", "Account activated successfully!"));
    }

    @GetMapping("/activation/{email}")
    @Transactional
    public ResponseEntity<?> activateAccount(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        auditLog.setEmail(email);
     
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            auditLog.setUserId(user.getId());
            auditLog.setUserName(user.getUsername());
            auditLog.setFullName(user.getFullName());
            auditLog.setIdentifyNum(user.getIdentifyNum());

            if (!user.getStatus().equals(User.status.ACTIVE.toString())) {
                user.setStatus(User.status.ACTIVE.toString());
                userRepository.save(user);
                auditLog.setDetails("User account activated: " + user.getEmail());
                rabbitTemplate.convertAndSend("appExchange", "activation.key", auditLog);
                return ResponseEntity.ok(Collections.singletonMap("message", "Account activated successfully!"));
            }
            auditLog.setDetails("Activation attempt for already active account: " + user.getEmail());
            rabbitTemplate.convertAndSend("appExchange", "activation.key", auditLog);
            return ResponseEntity.ok(Collections.singletonMap("message", "Account is already activated."));
        }
        auditLog.setDetails("Activation attempt for non-existent user: " + email);
        rabbitTemplate.convertAndSend("appExchange", "activation.key", auditLog);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found."));
    }


    @Transactional
    public void sendVerificationOtp(User user, String purpose) {
        verificationTokenRepository.deleteByUser(user);

        String otp = generateOtp();
        LocalDateTime expiryDate;

        String subject;
        String emailBody;

        auditLog.setUserId(user.getId());
        auditLog.setUserName(user.getUsername());
        auditLog.setFullName(user.getFullName());
        auditLog.setEmail(user.getEmail());
        auditLog.setIdentifyNum(user.getIdentifyNum());

        switch (purpose.toLowerCase()) {
            case "registration":
                expiryDate = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);
                subject = "Verify Your Email - OTP for Registration";
                emailBody = "Dear " + user.getFullName() + ",\n\n"
                        + "Your One-Time Password (OTP) for registration is: " + otp + "\n"
                        + "This OTP is valid for " + OTP_VALIDITY_MINUTES + " minutes.\n\n"
                        + "Regards,\nYour App Team";
                break;

            case "password_reset":
                expiryDate = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);
                subject = "Password Reset OTP";
                emailBody = "Dear " + user.getFullName() + ",\n\n"
                        + "Your OTP for password reset is: " + otp + "\n"
                        + "Valid for " + OTP_VALIDITY_MINUTES + " minutes.\n\n"
                        + "Regards,\nYour App Team";
                break;

            default:
                expiryDate = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);
                subject = "Your OTP";
                emailBody = "Dear " + user.getFullName() + ",\n\n"
                        + "Your OTP is: " + otp + "\n"
                        + "Valid for " + OTP_VALIDITY_MINUTES + " minutes.\n\n"
                        + "Regards,\nYour App Team";
        }

        VerificationToken verificationToken = new VerificationToken(otp, user, expiryDate);
        verificationTokenRepository.save(verificationToken);

        emailService.sendEmail(user.getEmail(), subject, emailBody);
        log.info("OTP {} (Purpose: {}) sent to {}", otp, purpose, user.getEmail());
        auditLog.setDetails("OTP " + otp + " sent to " + user.getEmail() + " for purpose: " + purpose);
        rabbitTemplate.convertAndSend("appExchange", "otp.key", auditLog); // Send
    }


    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Generates a digit from 0-9
        }
        auditLog.setDetails("Generated OTP: " + otp.toString());
        rabbitTemplate.convertAndSend("appExchange", "otp.key", auditLog);
        return otp.toString();
    }

//    @PostMapping("/resend-activation-otp")
//    public ResponseEntity<?> resendActivationOtp(@RequestBody ResendEmailRequest request) {
//        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User not found."));
//        }
//
//        User user = userOptional.get();
//
//        if (!user.getStatus().equals(User.status.PENDING_ACTIVATION.toString())) {
//            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "User is not in activation state."));
//        }
//
//        sendVerificationOtp(user, "activation");
//        return ResponseEntity.ok(Collections.singletonMap("message", "Activation OTP resent successfully."));
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        // Send RabbitMQ message after logout
        
        auditLog.setDetails("User logged out successfully.");
        rabbitTemplate.convertAndSend("appExchange", "logout.key", auditLog);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out"));
    }
}