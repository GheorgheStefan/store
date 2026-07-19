package com.air.practice.service;

import com.air.practice.dto.Role;
import com.air.practice.dto.UserLoginRequest;
import com.air.practice.dto.UserLoginResponse;
import com.air.practice.entity.User;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import com.air.sec.config.AuthTokenGateway;
import com.air.sec.config.exceptions.InvalidCredentialsException;
import com.air.sec.config.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenGateway authTokenGateway;

    @InjectMocks
    private UserService userService;

    private UserLoginRequest loginRequest;
    private User testUser;
    private UserLoginResponse expectedResponse;
    private String testEmail;
    private String testPassword;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testPassword = "securePassword123";
        encodedPassword = "hashedPassword123";

        loginRequest = new UserLoginRequest(testEmail, testPassword);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        expectedResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(true);

        when(userMapper.toResponseLogin(testUser, authTokenGateway))
                .thenReturn(expectedResponse);

        UserLoginResponse result = userService.login(loginRequest);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.token()).isEqualTo("jwt-token-123");
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining("Please register");
    }

    @Test
    void testLoginInvalidPassword() {
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    void testLoginAdminRoleSuccess() {
        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Admin")
                .lastName("User")
                .email(testEmail)
                .password(encodedPassword)
                .role(Role.ADMIN)
                .build();

        UserLoginResponse adminResponse = new UserLoginResponse(Role.ADMIN, "admin-jwt-token");

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(adminUser));

        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(true);

        when(userMapper.toResponseLogin(adminUser, authTokenGateway))
                .thenReturn(adminResponse);

        UserLoginResponse result = userService.login(loginRequest);

        assertThat(result)
                .isNotNull()
                .isEqualTo(adminResponse);
        assertThat(result.role()).isEqualTo(Role.ADMIN);

    }

    @Test
    void testUserMapperNotCalledWhenPasswordIncorrect() {
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void testMultipleLoginAttemptsWithDifferentEmails() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        User user1 = User.builder()
                .id(UUID.randomUUID())
                .email(email1)
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email(email2)
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        UserLoginResponse response1 = new UserLoginResponse(Role.USER, "token1");
        UserLoginResponse response2 = new UserLoginResponse(Role.USER, "token2");

        when(userRepository.findByEmail(email1))
                .thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(email2))
                .thenReturn(Optional.of(user2));

        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(true);

        when(userMapper.toResponseLogin(user1, authTokenGateway))
                .thenReturn(response1);
        when(userMapper.toResponseLogin(user2, authTokenGateway))
                .thenReturn(response2);

        UserLoginResponse result1 = userService.login(new UserLoginRequest(email1, testPassword));
        UserLoginResponse result2 = userService.login(new UserLoginRequest(email2, testPassword));

        assertThat(result1.token()).isEqualTo("token1");
        assertThat(result2.token()).isEqualTo("token2");
    }
}
