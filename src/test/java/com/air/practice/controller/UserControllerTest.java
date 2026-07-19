package com.air.practice.controller;

import com.air.practice.dto.Role;
import com.air.practice.dto.users.UserDetailsResponse;
import com.air.practice.dto.users.UserLoginRequest;
import com.air.practice.dto.users.UserLoginResponse;
import com.air.practice.dto.users.UserRegisterRequest;
import com.air.practice.dto.users.UserRegisterResponse;
import com.air.practice.dto.users.UserUpdateRequest;
import com.air.practice.service.UserService;
import com.air.sec.config.exceptions.EmailAlreadyExistsException;
import com.air.sec.config.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .build();
    }

    @Test
    void register_shouldReturnCreatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRegisterResponse response = new UserRegisterResponse(
                userId,
                "Stefan",
                "Gheorghe",
                "stefan@example.com"
        );

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Stefan",
                                  "lastName": "Gheorghe",
                                  "password": "password123",
                                  "email": "stefan@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("Stefan"))
                .andExpect(jsonPath("$.lastName").value("Gheorghe"))
                .andExpect(jsonPath("$.email").value("stefan@example.com"));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(new UserLoginResponse(Role.USER, "jwt-token-123"));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "stefan@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void userDetails_shouldReturnUserDetails() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetailsResponse response = new UserDetailsResponse(
                "Stefan",
                "Gheorghe",
                "stefan@example.com",
                Role.USER
        );

        when(userService.getUserDetails(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Stefan"))
                .andExpect(jsonPath("$.lastName").value("Gheorghe"))
                .andExpect(jsonPath("$.email").value("stefan@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void userDetailsList_shouldReturnAllUsers() throws Exception {
        UserDetailsResponse first = new UserDetailsResponse("Stefan", "Gheorghe", "stefan@example.com", Role.USER);
        UserDetailsResponse second = new UserDetailsResponse("Jane", "Doe", "jane@example.com", Role.ADMIN);

        when(userService.getUserDetailsList()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Stefan"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUserDetails() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetailsResponse response = new UserDetailsResponse(
                "Updated",
                "Name",
                "stefan@example.com",
                Role.USER
        );

        when(userService.updateUser(eq(userId), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Updated",
                                  "lastName": "Name"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService).deleteUser(userId);
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestBodyIsInvalidJson() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("User with email stefan@example.com already exists!"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Stefan",
                                  "lastName": "Gheorghe",
                                  "password": "password123",
                                  "email": "stefan@example.com"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void userDetails_shouldReturnNotFound_whenServiceThrows() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserDetails(userId)).thenThrow(new UserNotFoundException("User not found."));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}
