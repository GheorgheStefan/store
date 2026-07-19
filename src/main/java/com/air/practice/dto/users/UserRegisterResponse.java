package com.air.practice.dto.users;

import java.util.UUID;

public record UserRegisterResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {}
