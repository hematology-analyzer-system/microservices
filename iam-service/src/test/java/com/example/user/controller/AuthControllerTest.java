package com.example.user.controller;

import com.example.user.config.AuditTestConfig;
import com.example.user.dto.auth.AuthRequest;
import com.example.user.dto.register.RegisterRequest;
import com.example.user.model.User;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.UserRepository;
import com.example.user.security.JwtService;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(AuditTestConfig.class)
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authManager;

    @MockitoBean(name = "auditorProvider")
    private AuditorAware<UserAuditInfo> auditorAware;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        given(auditorAware.getCurrentAuditor()).willReturn(Optional.of(
                new UserAuditInfo(999L, "Test User", "test@example.com", "999999999")
        ));
    }

    @Test
    public void AuthController_register_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("0123456789");
        request.setGender("Male");
        request.setDateOfBirth("1990-01-01");
        request.setAge(34);
        request.setAddress("123 Main St");
        request.setPassword("123456");
//        request.setStatus("ACTIVE");
        request.setIdentifyNum("ID123456");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByPhone(request.getPhone())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encoded-password");

        // Act
        ResultActions response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    public void AuthController_register_EmailExists_ShouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("0123456789");
        request.setGender("Male");
        request.setDateOfBirth("1990-01-01");
        request.setAge(34);
        request.setAddress("123 Main St");
        request.setPassword("123456");
//        request.setStatus("ACTIVE");
        request.setIdentifyNum("ID123456");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        ResultActions response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Email already exists"));

    }

    @Test
    public void AuthController_register_PhoneExists_ShouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("0123456789");
        request.setGender("Male");
        request.setDateOfBirth("1990-01-01");
        request.setAge(34);
        request.setAddress("123 Main St");
        request.setPassword("123456");
//        request.setStatus("ACTIVE");
        request.setIdentifyNum("ID123456");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByPhone(request.getPhone())).willReturn(true);

        ResultActions response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Phone already exists"));

    }

    @Test
    public void AuthController_login_ShouldReturnToken() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("john.doe@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setFullName("Nguyễn Văn A");
        user.setEmail(request.getUsername());
        user.setPhone("00000000");
        user.setIdentifyNum("00000000");
        user.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        user.setGender("Nam");
        user.setAge(32);
        user.setDate_of_Birth("1/1/1992");
        user.setCreate_at(LocalDateTime.now());
        user.setUpdate_at(LocalDateTime.now());
        user.setPassword("$2a$10$encodedPassword"); // Giả mã hóa rồi

        String token = "mocked-jwt-token";

        // Mock authentication + repository + jwtService
        given(authManager.authenticate(Mockito.any(Authentication.class))).willReturn(mock(Authentication.class));
        given(userRepository.findByEmail(request.getUsername())).willReturn(user);
        given(jwtService.generateToken(user)).willReturn(token);

        // Act
        ResultActions response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresIn").value(86400));

    }

    @Test
    public void AuthController_login_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("wrong@example.com");
        request.setPassword("wrongpass");

        given(authManager.authenticate(Mockito.any(Authentication.class)))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResultActions response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        response.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid credentials"));
    }




}
