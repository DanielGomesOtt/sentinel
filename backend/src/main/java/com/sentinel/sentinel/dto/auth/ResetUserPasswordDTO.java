package com.sentinel.sentinel.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetUserPasswordDTO(
        @NotBlank(message = "Reset code is required")
        String code,
        @NotBlank(message = "New password is required")
        String newPassword,
        @NotBlank(message = "E-mail is required")
        String email
) {
}
