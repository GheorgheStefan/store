package com.air.practice.dto.users;

import com.air.practice.dto.Role;

public record UserDetailsResponse(
        String firstName,
        String lastName,
        String email,
        Role role
) {}
