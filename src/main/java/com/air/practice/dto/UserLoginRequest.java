package com.air.practice.dto;

public record UserLoginRequest(
        String email,
        String password
) {
}
