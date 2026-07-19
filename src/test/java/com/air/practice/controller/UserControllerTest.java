package com.air.practice.controller;

import com.air.practice.dto.UserRegisterRequest;
import com.air.practice.dto.UserRegisterResponse;
import com.air.practice.service.UserService;
import com.air.sec.config.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    void register_shouldReturnRegisteredUser() throws Exception {
        UUID userId = UUID.randomUUID();

        UserRegisterResponse response = new UserRegisterResponse(
                userId,
                "Stefan",
                "Gheorghe",
                "stefan@example.com"
        );

        when(userService.register(any(UserRegisterRequest.class)))
                .thenReturn(response);

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("Stefan"))
                .andExpect(jsonPath("$.lastName").value("Gheorghe"))
                .andExpect(jsonPath("$.email").value("stefan@example.com"));

        verify(userService).register(any(UserRegisterRequest.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestBodyIsInvalidJson()
            throws Exception {

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnConflict_whenEmailAlreadyExists()
            throws Exception {

        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException(
                        "User with email stefan@example.com already exists!"
                ));

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
}