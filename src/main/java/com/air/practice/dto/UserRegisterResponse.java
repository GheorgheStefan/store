package com.air.practice.dto;

import java.util.UUID;

public record UserRegisterResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {}
