package com.air.practice.dto.users;

public record UserRegisterRequest (
        String firstName,
        String lastName,
        String password,
        String email
) {}
