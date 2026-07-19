package com.air.practice.repository;

import com.air.practice.dto.Role;
import com.air.practice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Stefan")
                .lastName("Gheorghe")
                .password("encoded-password")
                .email("stefan@example.com")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should persist user with unique email")
    void save_shouldPersistUser() {
        when(userRepository.saveAndFlush(testUser)).thenReturn(testUser);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User savedUser = userRepository.saveAndFlush(testUser);

        assertNotNull(savedUser.getId());
        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        when(userRepository.existsByEmail("stefan@example.com")).thenReturn(true);

        boolean exists = userRepository.existsByEmail("stefan@example.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        when(userRepository.existsByEmail("missing@example.com")).thenReturn(false);

        boolean exists = userRepository.existsByEmail("missing@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should reject duplicate email")
    void save_shouldRejectDuplicateEmail() {
        User duplicate = User.builder()
                .firstName("Stefan")
                .lastName("Gheorghe")
                .password("encoded-password")
                .email("stefan@example.com")
                .role(Role.USER)
                .build();

        when(userRepository.saveAndFlush(duplicate))
                .thenThrow(new DataIntegrityViolationException("Duplicate email"));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(duplicate)
        );
    }
}
