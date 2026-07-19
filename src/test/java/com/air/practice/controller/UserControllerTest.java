//package com.air.practice.controller;
//
//import com.air.practice.dto.UserRegisterRequest;
//import com.air.practice.dto.UserRegisterResponse;
//import com.air.practice.service.UserService;
//import com.air.sec.config.exceptions.EmailAlreadyExistsException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setUp() {
//        UserController userController = new UserController(userService);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    @Test
//    void register_shouldReturnRegisteredUser() throws Exception {
//        UUID userId = UUID.randomUUID();
//
//        UserRegisterRequest request = new UserRegisterRequest(
//                "Stefan",
//                "Gheorghe",
//                "password123",
//                "stefan@example.com"
//        );
//
//        UserRegisterResponse response = new UserRegisterResponse(
//                userId,
//                request.firstName(),
//                request.lastName(),
//                request.email()
//        );
//
//        when(userService.register(request)).thenReturn(response);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                  "firstName": "Stefan",
//                                  "lastName": "Gheorghe",
//                                  "password": "password123",
//                                  "email": "stefan@example.com"
//                                }
//                                """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(userId.toString()))
//                .andExpect(jsonPath("$.firstName").value("Stefan"))
//                .andExpect(jsonPath("$.lastName").value("Gheorghe"))
//                .andExpect(jsonPath("$.email").value("stefan@example.com"));
//    }
//
//    @Test
//    void register_shouldReturnBadRequest_whenRequestBodyIsInvalidJson() throws Exception {
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{invalid-json}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void register_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
//        when(userRepository.existsByEmail(request.email())).thenReturn(true);
//
//        EmailAlreadyExistsException exception = assertThrows(
//                EmailAlreadyExistsException.class,
//                () -> userService.register(request)
//        );
//
//        assertEquals(
//                "User with email " + request.email() + " already exists!",
//                exception.getMessage()
//        );
//    }
//}
