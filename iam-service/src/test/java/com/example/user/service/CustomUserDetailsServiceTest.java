package com.example.user.service;

import com.example.user.model.User;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private AuditorAware<UserAuditInfo> auditorAware;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of(new UserAuditInfo(
                        99L, "Test User", "test@example.com", "999999999"
                )));
    }

    @Test
    public void CustomUserDetailsServiceTest_loadUserByUsername_ReturnsUserDetails_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");

        Mockito.when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo(email);
        Mockito.verify(userRepository, Mockito.times(1)).findByEmailWithRoles(email);
    }

    @Test
    public void CustomUserDetailsServiceTest_loadUserByUsername_ThrowsException_WhenUserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        Mockito.when(userRepository.findByEmailWithRoles(email)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        Mockito.verify(userRepository, Mockito.times(1)).findByEmailWithRoles(email);
    }


}
