package com.air.practice.dto.users;

public record UserLoginRequest(
        String email,
        String password
) {
}
