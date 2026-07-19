package com.air.practice.dto;

public record UserDetailsResponse(
        String firstName,
        String lastName,
        String email,
        Role role
) {}
