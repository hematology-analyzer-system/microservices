package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @InjectMocks
    private UserService userService;

    @Test
    public void UserServiceTest_loadUserByUsername_ReturnsUserDetails_WhenUserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo(email);
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(email);
    }

    @Test
    public void UserServiceTest_loadUserByUsername_ThrowsException_WhenUserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(email);
    }


}
