package com.sentinel.sentinel.dto.system_integration;

import jakarta.validation.constraints.NotBlank;

public record ClientAuthDTO(
        @NotBlank String clientId,
        @NotBlank String clientSecret
) {
}
