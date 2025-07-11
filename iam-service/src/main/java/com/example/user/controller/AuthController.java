package com.example.user.controller;
import com.example.user.dto.auth.AuthRequest;
import com.example.user.dto.register.RegisterRequest;
//import com.example.user.model.Role;
import com.example.user.model.User;
//import com.example.user.repository.RoleRepository;


import com.example.user.repository.UserRepository;
import com.example.user.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            var user = userRepository.findByEmail(request.getUsername());
            if (user == null) {
                log.error("User not found: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            // Generate JWT
            var token = jwtService.generateToken(user);
            log.info("JWT token generated for user: {}", request.getUsername());

            // Set JWT as HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false) //  true in production with HTTPS
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Lax")
                    .build();

            // Set cookie in header
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Return response body (optional, can omit token here for security)
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Login successful");
            body.put("username", user.getUsername());
            body.put("token", token);
            body.put("expiresIn", 86400); // Optional

            return ResponseEntity.ok(body);

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Login failed"));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "Email already exists")
            );
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "Phone already exists")
            );
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setDate_of_Birth(request.getDateOfBirth());
        user.setAge(request.getAge());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setStatus(request.getStatus());
        user.setIdentifyNum(request.getIdentifyNum());

        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully"));
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



