package com.example.user.controller;
import com.example.user.dto.auth.AuthRequest;
import com.example.user.dto.register.RegisterRequest;
//import com.example.user.model.Role;
import com.example.user.model.User;
//import com.example.user.repository.RoleRepository;


import com.example.user.repository.UserRepository;
import com.example.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthenticationManager authManager;
//    private final JwtService jwtService;
//    private final UserRepository userRepository;
////    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
//        //authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
//
//        System.out.println("Login attempt with username: " + request.getUsername());
//        System.out.println("Password received: " + request.getPassword());
//
//        var user = userRepository.findByEmail(request.getUsername());
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        var token = jwtService.generateToken(user);
//        return ResponseEntity.ok(Collections.singletonMap("token", token));
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            return ResponseEntity.badRequest().body("Email already exists");
//        }
//        if (userRepository.existsByPhone(request.getPhone())) {
//            return ResponseEntity.badRequest().body("Phone already exists");
//        }
//
////        Role defaultRole = roleRepository.findByName("Lab users")
////                .orElseGet(() -> {
////                    Role newRole = new Role();
////                    newRole.setName("Lab users");
////                    return roleRepository.save(newRole);
////                });
//
//        User user = new User();
//        user.setFullName(request.getFullName());
//        user.setEmail(request.getEmail());
//        user.setPhone(request.getPhone());
//        user.setGender(request.getGender());
//        user.setDate_of_Birth(request.getDateOfBirth());
//        user.setAge(request.getAge());
//        user.setAddress(request.getAddress());
//        user.setPassword(passwordEncoder.encode(request.getPassword())); // use encoder
//        user.setStatus(request.getStatus());
//        user.setIdentifyNum(request.getIdentifyNum());
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok("User registered successfully");
//    }
//
//}

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
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            // Authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            log.info("Authentication successful for user: {}", request.getUsername());

            var user = userRepository.findByEmail(request.getUsername());
            if (user == null) {
                log.error("User not found: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            var token = jwtService.generateToken(user);
            log.info("JWT token generated for user: {}", request.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("username", user.getUsername());
            response.put("expiresIn", 86400);

            return ResponseEntity.ok(response);

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
        user.setStatus(request.getStatus());
        user.setIdentifyNum(request.getIdentifyNum());

        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "User registered successfully"));
    }
}


