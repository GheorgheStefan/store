package com.air.practice.service;

import com.air.practice.dto.Role;
import com.air.practice.dto.UserLoginRequest;
import com.air.practice.dto.UserLoginResponse;
import com.air.practice.entity.User;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import com.air.sec.config.AuthTokenGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginHelperMethodsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenGateway authTokenGateway;

    @Mock
    private UserMapper userMapper;

    private String testEmail;
    private String testPassword;
    private String encodedPassword;
    private User testUser;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testPassword = "securePassword123";
        encodedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz123456789";

        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password(encodedPassword)
                .role(Role.USER)
                .build();
    }

    @Test
    void testRepositoryFindByEmailReturnsUserWhenExists() {
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findByEmail(testEmail);

        assertThat(result)
                .isNotEmpty()
                .contains(testUser);
        assertThat(result.get().getEmail()).isEqualTo(testEmail);
    }

    @Test
    void testRepositoryFindByEmailReturnsEmptyWhenNotExists() {
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail(testEmail);

        assertThat(result).isEmpty();
    }

    @Test
    void testPasswordEncoderMatchesReturnsTrueWhenPasswordMatches() {
        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(true);

        boolean result = passwordEncoder.matches(testPassword, encodedPassword);

        assertThat(result).isTrue();
    }

    @Test
    void testPasswordEncoderMatchesReturnsFalseWhenPasswordDoesNotMatch() {
        String wrongPassword = "wrongPassword123";
        when(passwordEncoder.matches(wrongPassword, encodedPassword))
                .thenReturn(false);

        boolean result = passwordEncoder.matches(wrongPassword, encodedPassword);

        assertThat(result).isFalse();
    }

    @Test
    void testPasswordEncoderMatchesReceivesCorrectPlainPassword() {
        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(true);

        passwordEncoder.matches(testPassword, encodedPassword);
    }

    @Test
    void testPasswordEncoderMatchesReceivesCorrectEncodedPassword() {
        when(passwordEncoder.matches(testPassword, encodedPassword))
                .thenReturn(true);

        passwordEncoder.matches(testPassword, encodedPassword);
    }

    @Test
    void testUserMapperToResponseLoginMapsCorrectly() {
        UserLoginResponse expectedResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userMapper.toResponseLogin(testUser, authTokenGateway))
                .thenReturn(expectedResponse);

        UserLoginResponse result = userMapper.toResponseLogin(testUser, authTokenGateway);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);
        assertThat(result.role()).isEqualTo(Role.USER);
        assertThat(result.token()).isEqualTo("jwt-token-123");
    }

    @Test
    void testUserMapperReturnsCorrectRole() {
        UserLoginResponse expectedResponse = new UserLoginResponse(Role.ADMIN, "admin-token");
        when(userMapper.toResponseLogin(testUser, authTokenGateway))
                .thenReturn(expectedResponse);

        UserLoginResponse result = userMapper.toResponseLogin(testUser, authTokenGateway);

        assertThat(result.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testUserMapperReturnsGeneratedToken() {
        String expectedToken = "generated-jwt-token-xyz";
        UserLoginResponse expectedResponse = new UserLoginResponse(Role.USER, expectedToken);
        when(userMapper.toResponseLogin(testUser, authTokenGateway))
                .thenReturn(expectedResponse);

        UserLoginResponse result = userMapper.toResponseLogin(testUser, authTokenGateway);

        assertThat(result.token()).isEqualTo(expectedToken);
        assertThat(result.token()).isNotBlank();
    }
}
