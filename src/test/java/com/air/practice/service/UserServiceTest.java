package com.air.practice.service;

import com.air.practice.dto.Role;
import com.air.practice.dto.UserRegisterRequest;
import com.air.practice.dto.UserRegisterResponse;
import com.air.practice.entity.User;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new UserRegisterRequest(
                "Stefan",
                "Gheorghe",
                "password123",
                "stefan@example.com"
        );
    }

    @Test
    void register_shouldReturnRegisteredUser_whenEmailDoesNotExist() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password("encoded-password")
                .email(request.email())
                .role(Role.USER)
                .build();

        UserRegisterResponse expectedResponse = new UserRegisterResponse(
                userId,
                request.firstName(),
                request.lastName(),
                request.email()
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.requestToEntity(request, passwordEncoder)).thenReturn(user);
        when(userMapper.toResponseRegister(user)).thenReturn(expectedResponse);

        UserRegisterResponse actualResponse = userService.register(request);

        assertEquals(expectedResponse, actualResponse);
    }
}
