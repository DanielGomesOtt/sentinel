package com.sentinel.sentinel.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordEmail(
        @NotBlank(message = "Email field is required.")
        String email
) {
}
