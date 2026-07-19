package com.air.practice.service;

import com.air.practice.dto.Role;
import com.air.practice.dto.users.UserDetailsResponse;
import com.air.practice.dto.users.UserRegisterRequest;
import com.air.practice.dto.users.UserRegisterResponse;
import com.air.practice.dto.users.UserUpdateRequest;
import com.air.practice.entity.User;
import com.air.practice.mapper.UserMapper;
import com.air.practice.repository.UserRepository;
import com.air.sec.config.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .firstName("Stefan")
                .lastName("Gheorghe")
                .password("encoded-password")
                .email("stefan@example.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_shouldReturnRegisteredUser_whenEmailDoesNotExist() {
        var request = new UserRegisterRequest(
                "Stefan",
                "Gheorghe",
                "password123",
                "stefan@example.com"
        );

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

    @Test
    void getUserDetails_shouldReturnUserDetails_whenUserExists() {
        UserDetailsResponse expectedResponse = new UserDetailsResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userMapper.toResponseDetails(user)).thenReturn(expectedResponse);

        UserDetailsResponse actualResponse = userService.getUserDetails(userId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getUserDetails_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDetails(userId));
    }

    @Test
    void getUserDetailsList_shouldReturnMappedUsers() {
        User secondUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Doe")
                .password("encoded-password-2")
                .email("jane@example.com")
                .role(Role.ADMIN)
                .build();

        UserDetailsResponse firstResponse = new UserDetailsResponse("Stefan", "Gheorghe", "stefan@example.com", Role.USER);
        UserDetailsResponse secondResponse = new UserDetailsResponse("Jane", "Doe", "jane@example.com", Role.ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user, secondUser));
        when(userMapper.toResponseDetails(user)).thenReturn(firstResponse);
        when(userMapper.toResponseDetails(secondUser)).thenReturn(secondResponse);

        var actualResponse = userService.getUserDetailsList();

        assertEquals(List.of(firstResponse, secondResponse), actualResponse);
    }

    @Test
    void deleteUser_shouldDelete_whenUserExists() {
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void updateUser_shouldReturnUpdatedDetails_whenUserExists() {
        var request = new UserUpdateRequest("Updated", "Name");

        User updatedUser = User.builder()
                .id(userId)
                .firstName("Updated")
                .lastName("Name")
                .password(user.getPassword())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        UserDetailsResponse expectedResponse = new UserDetailsResponse(
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail(),
                updatedUser.getRole()
        );

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userMapper.updateEntityFromRequest(user, request)).thenReturn(updatedUser);
        when(userMapper.toResponseDetails(updatedUser)).thenReturn(expectedResponse);

        UserDetailsResponse actualResponse = userService.updateUser(userId, request);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateUser_shouldThrowWhenUserDoesNotExist() {
        var request = new UserUpdateRequest("Updated", "Name");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, request));
        verify(userRepository, never()).save(user);
    }
}
