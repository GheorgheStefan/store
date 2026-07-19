package com.air.practice.dto;

public record UserRegisterRequest (
        String firstName,
        String lastName,
        String password,
        String email
) {}
