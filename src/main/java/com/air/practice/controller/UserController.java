package com.air.practice.controller;

import com.air.practice.dto.users.UserDetailsResponse;
import com.air.practice.dto.users.UserLoginRequest;
import com.air.practice.dto.users.UserLoginResponse;
import com.air.practice.dto.users.UserRegisterRequest;
import com.air.practice.dto.users.UserRegisterResponse;
import com.air.practice.dto.users.UserUpdateRequest;
import com.air.practice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints for registration, authentication, and profile management")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserRegisterResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details")
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(userRegisterRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserLoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User login credentials")
            @Valid @RequestBody UserLoginRequest userLoginRequest
    ) {
        return ResponseEntity.ok(
                userService.login(userLoginRequest)
        );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Get user details", description = "Retrieve user profile information by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDetailsResponse> userDetails(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                userService.getUserDetails(userId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "List all users", description = "Retrieve a list of all users (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users list retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<List<UserDetailsResponse>> userDetailsList() {
        return ResponseEntity.ok(
                userService.getUserDetailsList()
        );
    }

    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Update user profile", description = "Update user first name and last name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDetailsResponse> updateUser(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user information")
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        return ResponseEntity.ok(
                userService.updateUser(userId, userUpdateRequest)
        );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer")
    @Operation(summary = "Delete user", description = "Delete a user account by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId
    ) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
