package com.air.practice.dto;

public record UserLoginResponse(
        Role role,
        String token
) {}
