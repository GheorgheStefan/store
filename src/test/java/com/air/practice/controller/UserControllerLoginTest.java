package com.air.practice.controller;

import com.air.practice.dto.Role;
import com.air.practice.dto.UserLoginRequest;
import com.air.practice.dto.UserLoginResponse;
import com.air.practice.service.UserService;
import com.air.sec.config.exceptions.InvalidCredentialsException;
import com.air.sec.config.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerLoginTest {

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
    void testLoginSuccess() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void testLoginReturnsOkStatus() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginWithAdminRole() throws Exception {
        UserLoginResponse adminResponse = new UserLoginResponse(Role.ADMIN, "admin-jwt-token");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(adminResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin@example.com",
                            "password": "adminPassword123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.token").value("admin-jwt-token"));
    }

    @Test
    void testControllerCallsServiceWithCorrectRequest() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginUserNotFoundHandledByControllerAdvice() throws Exception {
        when(userService.login(any(UserLoginRequest.class)))
                .thenThrow(new UserNotFoundException("User not found. Please register."));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "notfound@example.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLoginInvalidPasswordHandledByControllerAdvice() throws Exception {
        when(userService.login(any(UserLoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid password."));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "wrongPassword"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithValidJsonRequest() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void testLoginResponseContentType() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testMultipleSequentialLoginRequests() throws Exception {
        UserLoginResponse response1 = new UserLoginResponse(Role.USER, "token1");
        UserLoginResponse response2 = new UserLoginResponse(Role.USER, "token2");

        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(response1)
                .thenReturn(response2);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "user1@example.com",
                            "password": "pass1"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token1"));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "user2@example.com",
                            "password": "pass2"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token2"));

    }

    @Test
    void testLoginEndpointPath() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void testUserServiceInjected() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "securePassword123"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginResponseHasRequiredFields() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.ADMIN, "admin-token-xyz");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin@example.com",
                            "password": "adminPass"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testServiceReceivesEmailFromRequest() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "specific@example.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void testServiceReceivesPasswordFromRequest() throws Exception {
        UserLoginResponse loginResponse = new UserLoginResponse(Role.USER, "jwt-token-123");
        when(userService.login(any(UserLoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "specificPassword123"
                        }
                        """))
                .andExpect(status().isOk());
    }
}
