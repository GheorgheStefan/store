package com.air.practice.dto.users;

import com.air.practice.dto.Role;

public record UserLoginResponse(
        Role role,
        String token
) {}
