package com.sentinel.sentinel.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserDTO(
        @NotBlank(message = "Name is a required field.")
        String name,
        @NotBlank(message = "Email is a required field.")
        String email,
        @NotBlank(message = "Password is a required field.")
        String password,
        @NotBlank(message = "Organization name is a required field.")
        String organizationName
) {
}
