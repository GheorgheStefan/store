package com.air.practice.controller;

import com.air.practice.dto.users.UserDetailsResponse;
import com.air.practice.dto.users.UserLoginRequest;
import com.air.practice.dto.users.UserLoginResponse;
import com.air.practice.dto.users.UserRegisterRequest;
import com.air.practice.dto.users.UserRegisterResponse;
import com.air.practice.dto.users.UserUpdateRequest;
import com.air.practice.service.UserService;
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
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserRegisterResponse> register(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(userRegisterRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(
            @Valid @RequestBody UserLoginRequest userLoginRequest
    ) {
        return ResponseEntity.ok(
                userService.login(userLoginRequest)
        );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailsResponse> userDetails(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                userService.getUserDetails(userId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDetailsResponse>> userDetailsList() {
        return ResponseEntity.ok(
                userService.getUserDetailsList()
        );
    }

    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailsResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        return ResponseEntity.ok(
                userService.updateUser(userId, userUpdateRequest)
        );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId
    ) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
