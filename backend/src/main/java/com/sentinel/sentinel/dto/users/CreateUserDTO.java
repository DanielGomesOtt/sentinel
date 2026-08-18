package com.sentinel.sentinel.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserDTO(
        @NotBlank(message = "Name is a required field.")
        String name,
        @NotBlank(message = "Email is a required field.")
        String email,
        @NotBlank(message = "Password is a required field.")
        String password,
        @NotNull(message = "Organization id is a required field.")
        Long organizationId,
        @NotBlank(message = "Role is a required field.")
        String role
) {
}
