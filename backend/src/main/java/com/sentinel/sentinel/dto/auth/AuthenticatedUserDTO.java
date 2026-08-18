package com.sentinel.sentinel.dto.auth;

import com.sentinel.sentinel.models.Users;

public record AuthenticatedUserDTO (
    Long id,
    String name,
    String email,
    String role,
    String token) {

    public AuthenticatedUserDTO(Users savedUser, String token) {
        this(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole().name(), token);
    }
}
